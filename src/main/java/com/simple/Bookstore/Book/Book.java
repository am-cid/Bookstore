package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Review.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @ElementCollection(targetClass = Genre.class)
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    private String frontImage;
    private String backImage;
    private String spineImage;
    @ElementCollection
    @CollectionTable(name = "book_content_images", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "image_url")
    private Set<String> contentImages = new HashSet<>();
    @ManyToMany(mappedBy = "savedBooks")
    private Set<Profile> savedByProfiles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }

    public void extendGenres(Set<Genre> newGenres) {
        this.genres.addAll(newGenres);
    }

    public void extendReviews(List<Review> newReviews) {
        this.reviews.addAll(newReviews);
    }

    public void extendContentImages(Set<String> newContentImages) {
        this.contentImages.addAll(newContentImages);
    }
}
