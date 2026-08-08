package com.lifesync.familyservice.repository;

import com.lifesync.familyservice.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    
    // Custom method: Find all family members belonging to a specific user
    List<FamilyMember> findByUsername(String username);
}