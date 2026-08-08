package com.lifesync.familyservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.lifesync.familyservice.GroceryItem;
import com.lifesync.familyservice.services.GroceryItemService;

import java.util.List;

@RestController
@RequestMapping("/api/family/groceries")
// Upgraded CORS to accept all standard methods so your React app isn't blocked
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
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

    // 4. PUT: Mark an item as purchased (FIXED TO MATCH REACT)
    @PutMapping("/{id}")
    public ResponseEntity<GroceryItem> markAsPurchased(@PathVariable @NonNull Long id, @RequestBody(required = false) GroceryItem itemDetails) {
        GroceryItem updatedItem = groceryItemService.markAsPurchased(id);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    // 5. DELETE: Remove an item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroceryItem(@PathVariable @NonNull Long id) {
        groceryItemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}