package com.lifesync.familyservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String relationship; // e.g., "Spouse", "Child", "Parent"
    
    private int age;

    // We store the username of the person who created this record
    // so we only show them THEIR family members, not everyone's!
    private String username; 
}