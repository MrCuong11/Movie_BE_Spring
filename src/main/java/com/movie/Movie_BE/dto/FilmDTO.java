package com.movie.Movie_BE.dto;

import com.movie.Movie_BE.Model.Episode;
import lombok.Data;
import java.util.List;

@Data
public class FilmDTO {
    private String name;
    private String slug;
    private String origin_name;
    private String poster_url;
    private String thumb_url;
    private int year;
    private String type;
    private String content;
    private String status;
    private String time;
    private String episode_current;
    private String episode_total;
    private int view;
    private List<String> actor;
    private List<String> director;
    private List<Long> categoryIds;
    private List<Long> countryIds;
    private List<Episode> episodes;
}
