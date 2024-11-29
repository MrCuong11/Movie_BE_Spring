package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository <Country, Long> {
}
