package com.lifesync.familyservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/family")
public class FamilyTestController {

    @GetMapping("/test")
    public ResponseEntity<String> testAccess() {
        return ResponseEntity.ok("Success! You have accessed the secure family-service data.");
    }
}