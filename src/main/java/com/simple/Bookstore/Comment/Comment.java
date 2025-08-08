package com.simple.Bookstore.Comment;

import com.simple.Bookstore.Review.Review;
import com.simple.Bookstore.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private boolean edited;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
        this.edited = false;
    }
}
