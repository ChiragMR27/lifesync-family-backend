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

@RestController
@RequestMapping("/api/family/groups")
@CrossOrigin(origins = "*")
public class FamilyGroupController {

    private final FamilyGroupRepository groupRepository;
    private final GroceryItemRepository groceryRepository; // NEW: Added to save groceries

    public FamilyGroupController(FamilyGroupRepository groupRepository, GroceryItemRepository groceryRepository) {
        this.groupRepository = groupRepository;
        this.groceryRepository = groceryRepository;
    }

    @GetMapping
    public ResponseEntity<List<FamilyGroup>> getAllGroups() {
        return ResponseEntity.ok(groupRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<FamilyGroup> createGroup(@RequestBody @NonNull FamilyGroup newGroup) {
        if (newGroup.getLeaderEmail() != null && !newGroup.getMembers().contains(newGroup.getLeaderEmail())) {
            newGroup.getMembers().add(newGroup.getLeaderEmail());
        }
        
        // Save the group first so the database assigns it a real ID
        FamilyGroup savedGroup = groupRepository.save(newGroup);

        // NEW: Auto-populate default items if this is a Grocery group
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

            // Save all 3 to the database instantly
            groceryRepository.saveAll(Arrays.asList(item1, item2, item3));
        }

        return ResponseEntity.ok(savedGroup);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<FamilyGroup> addMemberToGroup(@PathVariable @NonNull Long groupId, @RequestBody Map<String, String> request) {
        String email = request.get("email");
        return groupRepository.findById(groupId).map(group -> {
            if (!group.getMembers().contains(email)) {
                group.getMembers().add(email);
                groupRepository.save(group);
            }
            return ResponseEntity.ok(group);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{groupId}/leader")
    public ResponseEntity<FamilyGroup> transferLeadership(@PathVariable @NonNull Long groupId, @RequestBody Map<String, String> request) {
        String newLeaderEmail = request.get("newLeaderEmail");
        return groupRepository.findById(groupId).map(group -> {
            if (!group.getMembers().contains(newLeaderEmail)) {
                group.getMembers().add(newLeaderEmail);
            }
            group.setLeaderEmail(newLeaderEmail);
            return ResponseEntity.ok(groupRepository.save(group));
        }).orElse(ResponseEntity.notFound().build());
    }
}