package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.Favorite;
import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByFilmAndUser(Film film, User user);
    Page<Favorite> findByUser(User user, Pageable pageable);
}
