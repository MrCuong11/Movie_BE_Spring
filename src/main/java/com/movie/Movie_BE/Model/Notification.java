package com.movie.Movie_BE.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User user;

    private String message;
    private boolean isRead = false;
    private String actionType;
    private Long relatedId;

    private LocalDateTime createdAt = LocalDateTime.now();

}
