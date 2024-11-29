package com.movie.Movie_BE.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class HistorySummary extends FilmSummary {
    private LocalDateTime watchTime;
}
