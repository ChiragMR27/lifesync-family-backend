package com.lifesync.familyservice.controller;

import com.lifesync.familyservice.model.FamilyGroup;
import com.lifesync.familyservice.model.GroceryItem;
import com.lifesync.familyservice.repository.FamilyGroupRepository;
import com.lifesync.familyservice.repository.GroceryItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/family/groups")
@CrossOrigin(origins = "*")
public class FamilyGroupController {

    private final FamilyGroupRepository groupRepository;
    private final GroceryItemRepository groceryRepository;

    public FamilyGroupController(FamilyGroupRepository groupRepository, GroceryItemRepository groceryRepository) {
        this.groupRepository = groupRepository;
        this.groceryRepository = groceryRepository;
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
}