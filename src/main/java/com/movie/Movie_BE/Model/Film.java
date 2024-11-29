package com.movie.Movie_BE.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "films")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String slug;

    private String origin_name;
    private String poster_url;
    private String thumb_url;
    private int year;
    private String type;

    @Lob
    private String content;

    private String status;

    @Column(name = "timeFilm")
    private String time;

    private String episode_current;
    private String episode_total;
    private int view;


    @ElementCollection
    private List<String> actor;

    @ElementCollection
    private List<String> director;

    @ManyToMany
    @JoinTable(
            name = "film_categories",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "film_countries",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    private List<Country> countries;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Episode> episodes;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    @Embedded
    private Modified modified = new Modified();
}
