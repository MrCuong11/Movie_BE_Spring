package com.movie.Movie_BE.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Người dùng xem phim

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film; // Phim đã xem

    private LocalDateTime watchTime; // Thời gian xem phim
    @Column(name = "view_count", nullable = false)
    private int viewCount = 1;

}
