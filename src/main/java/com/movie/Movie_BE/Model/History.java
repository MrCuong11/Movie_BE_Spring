package com.movie.Movie_BE.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public LocalDateTime getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(LocalDateTime watchTime) {
        this.watchTime = watchTime;
    }
}
