package com.movie.Movie_BE.Repository;
import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.dto.FilmSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    Optional<Film> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsById(Long id);
    Page<Film> findByType(String type, Pageable pageable);

    Page<Film> findByCategories_Name(String categoryName, Pageable pageable);

    Page<Film> findByCountries_Name(String countryName, Pageable pageable);

    @Modifying // Cho JPA biết là đang sử dụng câu lệnh thay đổi dữ liệu (UPDATE, DELETE, hoặc INSERT).
    @Query("UPDATE Film f SET f.view = :view WHERE f.id = :filmId")
    void updateViewWithoutModified(@Param("filmId") Long filmId, @Param("view") int view);

//    @Query("SELECT f FROM Film f WHERE " +
//            "(LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(f.origin_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(REPLACE(f.slug, '-', ' ')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    List<Film> searchFilmsByKeyword(@Param("keyword") String keyword);

}


