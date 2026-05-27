package com.example.ibminnovate.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

@Entity

public class User implements UserDetails {
    @Id
    @GeneratedValue(generator = "custom-id-gen")
    @GenericGenerator(name = "custom-id-gen", strategy = "com.example.ibminnovate.model.CustomIdGenerator")
    private long ID;

    private String username; // Ensure this matches the field name in the database
    private String email;
    private String password;
    private String role;
    private boolean enabled = true; // Required by UserDetails

    private int exp = 0;
    private int level = 1;

    private String profilePicture;
    private String avatarUrl;
    private int rank;
    @ElementCollection
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "badge_id")
    @Column(name = "earned")
    private Map<String, Boolean> badges = new HashMap<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Progress> progressList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Achievement> achievementList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserCourse> userCourses;


    private String provider; // OAuth provider (e.g., "google")
    private String providerId; // Unique ID from Google
    private String userType;

    //To keep a streak history
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserStreak> userStreakLog;

    public User() {}

    public User(String username, String email, String profilePicture, String avatarUrl, int exp, int level, int rank) {
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
        this.avatarUrl = avatarUrl;
        this.exp = exp;
        this.level = level;
        this.rank = rank;
    }
    public User(String username, String email,String password, int exp) {
        this.username = username;
        this.email= email;
        this.password= password;
        this.exp = exp;

    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getRank() {return rank;}
    public void setRank(int rank) {this.rank = rank;}
    // Modify Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Map<String, Boolean> getBadges() {
        if (badges == null) {
            badges = new HashMap<>();
        }
        return badges;
    }

    public void setBadges(Map<String, Boolean> badges) {
        this.badges = badges;
    }

    // Getters and Setters
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Progress> getProgressList() {
        return progressList;
    }

    public void setProgressList(List<Progress> progressList) {
        this.progressList = progressList;
    }

    public List<Achievement> getAchievementList() {
        return achievementList;
    }

    public void setAchievementList(List<Achievement> achievementList) {
        this.achievementList = achievementList;
    }

    public List<UserCourse> getUserCourses() {
        return userCourses;
    }

    public void setUserCourses(List<UserCourse> userCourses) {
        this.userCourses = userCourses;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", exp=" + exp +
                ", level=" + level +
                ", profilePicture='" + profilePicture + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", rank=" + rank +
                ", progressList=" + progressList +
                ", achievementList=" + achievementList +
                ", userCourses=" + userCourses +
                ", provider='" + provider + '\'' +
                ", providerId='" + providerId + '\'' +
                ", userType='" + userType + '\'' +
                ", userStreakLog=" + userStreakLog +
                '}';
    }

    // UserDetails Interface Methods:

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the role(s) into authorities (permissions)
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;  // Custom condition can be added if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Custom condition can be added if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Custom condition can be added if needed
    }

    public void setUserStreakLog(List<UserStreak> userStreakLog) {
        this.userStreakLog = userStreakLog;
    }

    public List<UserStreak> getUserStreakLog() {
        return userStreakLog;
    }
}
