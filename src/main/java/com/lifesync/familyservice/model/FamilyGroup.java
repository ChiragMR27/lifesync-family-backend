package com.lifesync.familyservice.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "family_groups")
public class FamilyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; 
    private String badgeColor; 
    
    @Column(name = "leader_email")
    private String leaderEmail;

    // Stores members as a safe, high-capacity comma-separated text string in MySQL
    @Column(name = "member_emails", columnDefinition = "TEXT")
    private String memberEmails = "";

    public FamilyGroup() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBadgeColor() { return badgeColor; }
    public void setBadgeColor(String badgeColor) { this.badgeColor = badgeColor; }
    
    public String getLeaderEmail() { return leaderEmail; }
    public void setLeaderEmail(String leaderEmail) { this.leaderEmail = leaderEmail; }

    // Automatically converts comma-separated database text into a Java List for your controller
    public List<String> getMembers() {
        if (memberEmails == null || memberEmails.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(memberEmails.split(",")));
    }

    // Automatically converts your Java List back into a comma-separated string for MySQL
    public void setMembers(List<String> membersList) {
        if (membersList == null || membersList.isEmpty()) {
            this.memberEmails = "";
        } else {
            this.memberEmails = String.join(",", membersList);
        }
    }

    public int getMembersCount() { 
        List<String> list = getMembers();
        return list != null ? list.size() : 0; 
    }
}