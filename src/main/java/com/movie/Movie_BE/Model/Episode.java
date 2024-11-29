package com.movie.Movie_BE.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "episodes")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String slug;
    private String filename;
    private String link_embed;
    private String link_m3u8;

    @ManyToOne
    @JoinColumn(name = "film_id")
    @JsonIgnore
    private Film film;
}
