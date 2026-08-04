package com.lifesync.family_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifesync.family_service.GroceryItem;

import java.util.List;

@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    
    // Custom method to easily find items that haven't been purchased yet
    List<GroceryItem> findByIsPurchasedFalse();
}