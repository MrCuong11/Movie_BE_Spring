package com.movie.Movie_BE.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ApplicationState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long previousTotalElements;

    // Getters and setters
}

