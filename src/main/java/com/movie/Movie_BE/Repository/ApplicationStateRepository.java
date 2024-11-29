package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.ApplicationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationStateRepository extends JpaRepository<ApplicationState, Long> {
    Optional<ApplicationState> findTopByOrderByIdDesc();
}

