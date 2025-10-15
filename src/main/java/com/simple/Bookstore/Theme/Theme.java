package com.simple.Bookstore.Theme;

import com.simple.Bookstore.Profile.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToMany(mappedBy = "savedThemes")
    private Set<Profile> savedByProfiles = new HashSet<>();

    @OneToMany(mappedBy = "usedTheme")
    private Set<Profile> usedByProfiles = new HashSet<>();

    @Column(nullable = false)
    private boolean published = false;
    @Column(length = 6, nullable = false)
    private String base00;
    @Column(length = 6, nullable = false)
    private String base01;
    @Column(length = 6, nullable = false)
    private String base02;
    @Column(length = 6, nullable = false)
    private String base03;
    @Column(length = 6, nullable = false)
    private String base04;
    @Column(length = 6, nullable = false)
    private String base05;
    @Column(length = 6, nullable = false)
    private String base06;
    @Column(length = 6, nullable = false)
    private String base07;

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }

    public String getColorByIndex(int i) throws IllegalArgumentException {
        return switch (i) {
            case 0 ->
                    base00;
            case 1 ->
                    base01;
            case 2 ->
                    base02;
            case 3 ->
                    base03;
            case 4 ->
                    base04;
            case 5 ->
                    base05;
            case 6 ->
                    base06;
            case 7 ->
                    base07;
            default ->
                    throw new IllegalArgumentException("Invalid base color index: " + i);
        };
    }
}
