package com.movie.Movie_BE.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID tự tăng

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String token; // Lưu FCM Token


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Khóa ngoại liên kết tới User

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;
}
