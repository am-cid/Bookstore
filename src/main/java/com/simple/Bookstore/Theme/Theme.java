package com.simple.Bookstore.Theme;

import com.simple.Bookstore.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "themesInUse")
    private Set<User> usersUsing;

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
