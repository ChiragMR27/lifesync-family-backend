package com.lifesync.familyservice.repository;

import com.lifesync.familyservice.model.FamilyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyGroupRepository extends JpaRepository<FamilyGroup, Long> {
}