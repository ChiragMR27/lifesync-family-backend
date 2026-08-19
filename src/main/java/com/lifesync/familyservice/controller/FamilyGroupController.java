package com.lifesync.familyservice.controller;

import com.lifesync.familyservice.model.FamilyGroup;
import com.lifesync.familyservice.model.GroceryItem;
import com.lifesync.familyservice.model.GroupMessage;
import com.lifesync.familyservice.repository.FamilyGroupRepository;
import com.lifesync.familyservice.repository.GroceryItemRepository;
import com.lifesync.familyservice.repository.GroupMessageRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/family/groups")
@CrossOrigin(origins = "*")
public class FamilyGroupController {

    private final FamilyGroupRepository groupRepository;
    private final GroceryItemRepository groceryRepository;
    private final GroupMessageRepository groupMessageRepository; 
    
    // NEW: Background Notification Service & Storage
    private PushService pushService;
    private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

    public FamilyGroupController(FamilyGroupRepository groupRepository, GroceryItemRepository groceryRepository, GroupMessageRepository groupMessageRepository) {
        this.groupRepository = groupRepository;
        this.groceryRepository = groceryRepository;
        this.groupMessageRepository = groupMessageRepository;
        
        // Initialize Security Provider and Web Push Keys
        try {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            this.pushService = new PushService(
                "mailto:admin@lifesync.com",
                "BEl62iUYgUivxIkv69yViEuiBIa-Ib9-SkvMeAtA3LFgDzkrxZJjSgSnfckjBJuBkr3qBUYIHBQFLXYp5Nksh8U=",
                "aV_W3x8P08pQ80vR_17eB-zD9p5A5s2S5Q8G8t90AQA="
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // NEW: Endpoint for React to register a device for background notifications
    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestParam String email, @RequestBody Subscription subscription) {
        subscriptions.put(email, subscription);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FamilyGroup>> getUserGroups(@RequestParam String email) {
        List<FamilyGroup> allGroups = groupRepository.findAll();
        
        List<FamilyGroup> userGroups = allGroups.stream()
                .filter(group -> 
                    (group.getLeaderEmail() != null && group.getLeaderEmail().equals(email)) || 
                    group.getMembers().contains(email)
                )
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(userGroups);
    }

    @PostMapping
    public ResponseEntity<FamilyGroup> createGroup(@RequestBody @NonNull FamilyGroup newGroup) {
        List<String> members = newGroup.getMembers();
        if (newGroup.getLeaderEmail() != null && !members.contains(newGroup.getLeaderEmail())) {
            members.add(newGroup.getLeaderEmail());
            newGroup.setMembers(members); 
        }
        
        FamilyGroup savedGroup = groupRepository.save(newGroup);

        if ("Grocery".equalsIgnoreCase(savedGroup.getType())) {
            GroceryItem item1 = new GroceryItem();
            item1.setText("Organic Whole Milk (2L)");
            item1.setDefault(true);
            item1.setInCart(false);
            item1.setGroupId(savedGroup.getId());
            item1.setAddedBy(savedGroup.getLeaderEmail());

            GroceryItem item2 = new GroceryItem();
            item2.setText("Fresh Avocados (x4)");
            item2.setDefault(true);
            item2.setInCart(false);
            item2.setGroupId(savedGroup.getId());
            item2.setAddedBy(savedGroup.getLeaderEmail());

            GroceryItem item3 = new GroceryItem();
            item3.setText("Whole Wheat Bread");
            item3.setDefault(true);
            item3.setInCart(false);
            item3.setGroupId(savedGroup.getId());
            item3.setAddedBy(savedGroup.getLeaderEmail());

            groceryRepository.saveAll(Arrays.asList(item1, item2, item3));
        }

        return ResponseEntity.ok(savedGroup);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<FamilyGroup> addMemberToGroup(@PathVariable @NonNull Long groupId, @RequestBody Map<String, String> request) {
        String email = request.get("email");
        return groupRepository.findById(groupId).map(group -> {
            List<String> members = group.getMembers();
            if (!members.contains(email)) {
                members.add(email);
                group.setMembers(members); 
                groupRepository.save(group);
            }
            return ResponseEntity.ok(group);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<FamilyGroup> removeMemberFromGroup(
            @PathVariable @NonNull Long groupId, 
            @RequestParam String memberEmail, 
            @RequestParam String requesterEmail) {
        
        return groupRepository.findById(groupId).map(group -> {
            if (group.getLeaderEmail() != null && group.getLeaderEmail().equalsIgnoreCase(requesterEmail)) {
                List<String> members = group.getMembers();
                if (members.contains(memberEmail) && !memberEmail.equalsIgnoreCase(group.getLeaderEmail())) {
                    members.remove(memberEmail);
                    group.setMembers(members);
                    groupRepository.save(group);
                }
            }
            return ResponseEntity.ok(group);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{groupId}/leader")
    public ResponseEntity<FamilyGroup> transferLeadership(@PathVariable @NonNull Long groupId, @RequestBody Map<String, String> request) {
        String newLeaderEmail = request.get("newLeaderEmail");
        return groupRepository.findById(groupId).map(group -> {
            List<String> members = group.getMembers();
            if (!members.contains(newLeaderEmail)) {
                members.add(newLeaderEmail);
                group.setMembers(members);
            }
            group.setLeaderEmail(newLeaderEmail);
            return ResponseEntity.ok(groupRepository.save(group));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{groupId}/messages")
    public ResponseEntity<GroupMessage> sendGroupMessage(@PathVariable Long groupId, @RequestBody GroupMessage message) {
        message.setGroupId(groupId);
        GroupMessage savedMessage = groupMessageRepository.save(message);

        // NEW: Automatically alert all members who are subscribed
        groupRepository.findById(groupId).ifPresent(group -> {
            String payload = String.format("{\"title\": \"New message in %s\", \"body\": \"%s\"}", group.getName(), message.getText());
            for (String memberEmail : group.getMembers()) {
                if (!memberEmail.equalsIgnoreCase(message.getSenderEmail()) && subscriptions.containsKey(memberEmail)) {
                    try {
                        pushService.send(new Notification(subscriptions.get(memberEmail), payload));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<GroupMessage>> getGroupMessages(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupMessageRepository.findByGroupIdOrderByIdAsc(groupId));
    }
}	