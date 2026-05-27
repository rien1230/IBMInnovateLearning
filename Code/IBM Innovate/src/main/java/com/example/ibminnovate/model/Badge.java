package com.example.ibminnovate.model;

import jakarta.persistence.*;

@Entity
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // Specific Badge Name ie Level 1 - Name of Badge
    private String description;
    private String image;

    //Acts as a type tag
    @ManyToOne
    @JoinColumn(name = "badge_progression_id", nullable = false)
    private BadgeProgression badgeProgression;
    private int level;

    public Badge() {}
    public Badge(String name, String description, String image, int level, BadgeProgression progression) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.level = level;
        this.badgeProgression = progression;
    }

    public String getImagePath() {
        return "/static/images/badges/" + image;
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

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public BadgeProgression getBadgeProgression() {
        return badgeProgression;
    }
    public void setBadgeProgression(BadgeProgression badgeProgression) {
        this.badgeProgression = badgeProgression;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
