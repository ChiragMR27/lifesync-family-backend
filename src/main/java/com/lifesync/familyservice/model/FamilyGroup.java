package com.lifesync.familyservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
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

    // MAGIC FIX: This saves the emails as a JSON array in the same table, 
    // completely bypassing the Aiven Primary Key error!
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "member_emails")
    private List<String> members = new ArrayList<>();

    public FamilyGroup() {}

    // Getters and Setters
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

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public int getMembersCount() { 
        return members != null ? members.size() : 0; 
    }
}