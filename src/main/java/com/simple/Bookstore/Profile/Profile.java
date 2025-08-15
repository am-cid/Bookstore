package com.simple.Bookstore.Profile;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Theme.Theme;
import com.simple.Bookstore.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String displayName;
    private boolean isPublic;

    @ManyToMany
    @JoinTable(
            name = "profile_saved_books",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")

    )
    private Set<Book> savedBooks = new HashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Theme> ownedThemes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "profile_saved_themes",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<Theme> savedThemes = new HashSet<>();
}
