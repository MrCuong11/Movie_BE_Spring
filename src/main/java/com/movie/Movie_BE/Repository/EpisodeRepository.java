package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    Optional<List<Episode>> findByFilmSlug(String slug);
}

