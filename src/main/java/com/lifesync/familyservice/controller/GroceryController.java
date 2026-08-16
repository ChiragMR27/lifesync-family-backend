package com.lifesync.familyservice.controller;

import com.lifesync.familyservice.model.GroceryItem;
import com.lifesync.familyservice.repository.GroceryItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family/groups/{groupId}/groceries")
@CrossOrigin(origins = "*")
public class GroceryController {

    private final GroceryItemRepository groceryRepository;

    public GroceryController(GroceryItemRepository groceryRepository) {
        this.groceryRepository = groceryRepository;
    }

    // Load all groceries for this specific group
    @GetMapping
    public ResponseEntity<List<GroceryItem>> getGroceries(@PathVariable Long groupId) {
        return ResponseEntity.ok(groceryRepository.findByGroupId(groupId));
    }

    // Add a new grocery item
    @PostMapping
    public ResponseEntity<GroceryItem> addGrocery(@PathVariable Long groupId, @RequestBody GroceryItem item) {
        item.setGroupId(groupId);
        return ResponseEntity.ok(groceryRepository.save(item));
    }

    // Update an item (e.g., claiming it, moving to cart, unclaiming)
    @PutMapping("/{itemId}")
    public ResponseEntity<GroceryItem> updateGrocery(@PathVariable Long groupId, @PathVariable Long itemId, @RequestBody GroceryItem updatedItem) {
        return groceryRepository.findById(itemId).map(item -> {
            item.setDefault(updatedItem.isDefault());
            item.setInCart(updatedItem.isInCart());
            item.setAddedBy(updatedItem.getAddedBy());
            item.setClaimedBy(updatedItem.getClaimedBy());
            return ResponseEntity.ok(groceryRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete an item completely
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteGrocery(@PathVariable Long groupId, @PathVariable Long itemId) {
        groceryRepository.deleteById(itemId);
        return ResponseEntity.ok().build();
    }
}