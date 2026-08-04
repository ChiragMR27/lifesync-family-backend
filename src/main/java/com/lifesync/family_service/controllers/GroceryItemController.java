package com.lifesync.family_service.controllers;

import com.lifesync.family_service.GroceryItem;
import com.lifesync.family_service.services.GroceryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family/groceries")
@CrossOrigin(origins = "http://localhost:5173") // This is crucial for your React frontend!
public class GroceryItemController {

    @Autowired
    private GroceryItemService groceryItemService;

    // 1. GET: Fetch all items
    @GetMapping
    public ResponseEntity<List<GroceryItem>> getAllGroceries() {
        return new ResponseEntity<>(groceryItemService.getAllItems(), HttpStatus.OK);
    }

    // 2. GET: Fetch only pending items
    @GetMapping("/pending")
    public ResponseEntity<List<GroceryItem>> getPendingGroceries() {
        return new ResponseEntity<>(groceryItemService.getPendingItems(), HttpStatus.OK);
    }

    // 3. POST: Add a new item
    @PostMapping
    public ResponseEntity<GroceryItem> addGroceryItem(@RequestBody GroceryItem item) {
        GroceryItem savedItem = groceryItemService.addItem(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    // 4. PUT: Mark an item as purchased
    @PutMapping("/{id}/purchase")
    public ResponseEntity<GroceryItem> markAsPurchased(@PathVariable Long id) {
        GroceryItem updatedItem = groceryItemService.markAsPurchased(id);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    // 5. DELETE: Remove an item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroceryItem(@PathVariable Long id) {
        groceryItemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}