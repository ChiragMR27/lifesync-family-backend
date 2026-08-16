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

    // THE FIX: Forces Java and React to use the exact same name for this variable
    @JsonProperty("isDefault")
    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "in_cart")
    private boolean inCart;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "claimed_by")
    private String claimedBy;

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
}