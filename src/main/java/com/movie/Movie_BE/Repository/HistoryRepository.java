package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Model.History;
import com.movie.Movie_BE.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    Page<History> findByUser(User user, Pageable pageable);

    Optional<History> findByUserAndFilm(User user, Film film);

    List<History> findByUser(User user);
}
