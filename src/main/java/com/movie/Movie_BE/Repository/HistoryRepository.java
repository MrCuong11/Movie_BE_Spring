package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Model.History;
import com.movie.Movie_BE.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    Page<History> findByUser(User user, Pageable pageable);

    @Query("SELECT h FROM History h WHERE h.user = :user AND h.film = :film")
    Optional<History> findByUserAndFilm(@Param("user") User user, @Param("film") Film film);


    List<History> findByUser(User user);
    @Transactional
    @Modifying
    @Query("DELETE FROM History h WHERE h.film.id = :filmId")
    void deleteByFilmId(@Param("filmId") Long filmId);
}
