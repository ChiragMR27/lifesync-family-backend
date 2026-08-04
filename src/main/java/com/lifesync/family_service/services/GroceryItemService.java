package com.lifesync.family_service.services;

import com.lifesync.family_service.GroceryItem;
import com.lifesync.family_service.repositories.GroceryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroceryItemService {

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    // 1. Get all grocery items (history included)
    public List<GroceryItem> getAllItems() {
        return groceryItemRepository.findAll();
    }

    // 2. Get only items that need to be bought
    public List<GroceryItem> getPendingItems() {
        return groceryItemRepository.findByIsPurchasedFalse();
    }

    // 3. Add a new grocery item to the list
    public GroceryItem addItem(GroceryItem item) {
        // Ensure a new item always starts as NOT purchased
        item.setPurchased(false);
        return groceryItemRepository.save(item);
    }

    // 4. Check an item off the list (Mark as purchased)
    public GroceryItem markAsPurchased(Long id) {
        // Find the item, or throw an error if someone tries to update an ID that doesn't exist
        GroceryItem item = groceryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery item not found with id: " + id));
        
        item.setPurchased(true);
        return groceryItemRepository.save(item);
    }

    // 5. Delete an item completely
    public void deleteItem(Long id) {
        groceryItemRepository.deleteById(id);
    }
}