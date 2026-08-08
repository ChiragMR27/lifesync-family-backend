package com.lifesync.familyservice.controller;

import com.lifesync.familyservice.dto.FamilyMemberDto;
import com.lifesync.familyservice.model.FamilyMember;
import com.lifesync.familyservice.repository.FamilyMemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Allows your React frontend to connect later!
@RestController
@RequestMapping("/api/family")
public class FamilyController {

    private final FamilyMemberRepository repository;

    public FamilyController(FamilyMemberRepository repository) {
        this.repository = repository;
    }

    // Endpoint 1: Add a new family member
    @PostMapping
    public ResponseEntity<FamilyMember> addFamilyMember(@RequestBody FamilyMemberDto dto) {
        
        // 1. Get the username of the currently logged-in user from their token
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Map the DTO data to our Database Entity
        FamilyMember member = new FamilyMember();
        member.setName(dto.getName());
        member.setRelationship(dto.getRelationship());
        member.setAge(dto.getAge());
        
        // 3. SECURE IT: Lock this record to the current user!
        member.setUsername(currentUsername); 

        // 4. Save to the Aiven database
        FamilyMember savedMember = repository.save(member);
        return ResponseEntity.ok(savedMember);
    }

    // Endpoint 2: Fetch family members for the logged-in user
    @GetMapping
    public ResponseEntity<List<FamilyMember>> getMyFamilyMembers() {
        
        // 1. Find out who is making the request
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Ask the database for ONLY their family members
        List<FamilyMember> myFamily = repository.findByUsername(currentUsername);
        
        return ResponseEntity.ok(myFamily);
    }
}