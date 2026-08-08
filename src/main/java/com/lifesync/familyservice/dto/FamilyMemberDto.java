package com.lifesync.familyservice.dto;

import lombok.Data;

@Data
public class FamilyMemberDto {
    private String name;
    private String relationship;
    private int age;
}