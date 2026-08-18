package com.lifesync.familyservice.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "grocery_items")
public class GroceryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @JsonProperty("isDefault")
    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "in_cart")
    private boolean inCart;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "claimed_by")
    private String claimedBy;

    // NEW: Quantity tracking (e.g., 0.25, 2.0)
    @Column(name = "quantity")
    private Double quantity;

    // NEW: Unit tracking (e.g., "kg", "pcs", "liters")
    @Column(name = "unit")
    private String unit;

    public GroceryItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    @JsonProperty("isDefault")
    public boolean isDefault() { return isDefault; }
    
    @JsonProperty("isDefault")
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public boolean isInCart() { return inCart; }
    public void setInCart(boolean inCart) { this.inCart = inCart; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public String getClaimedBy() { return claimedBy; }
    public void setClaimedBy(String claimedBy) { this.claimedBy = claimedBy; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}