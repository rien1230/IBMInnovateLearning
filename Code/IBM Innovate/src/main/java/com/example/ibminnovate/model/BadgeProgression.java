package com.example.ibminnovate.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BadgeProgression {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "badgeProgression", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("level ASC")
    private List<Badge> badges = new ArrayList<>();

    // Helper to add badges with validation
    public void addBadge(Badge badge) {
        if (badges.stream().anyMatch(b -> b.getLevel() == badge.getLevel())) {
            throw new IllegalArgumentException("Level " + badge.getLevel() + " already exists in this progression!");
        }
        badge.setBadgeProgression(this);
        badges.add(badge);
    }

    //Constructors
    public BadgeProgression() {}
    public BadgeProgression(Long id, String name, String description, List<Badge> badges) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.badges = badges;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }
}
