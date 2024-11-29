package com.movie.Movie_BE.dto;

import lombok.Data;

@Data
public class FilmSummary {
    private Long id;
    private String name;
    private String slug;
    private String origin_name;
    private String poster_url;
    private String thumb_url;
    private int view;
    private int year;
}
