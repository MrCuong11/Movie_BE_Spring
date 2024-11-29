package com.movie.Movie_BE.Service;

import com.movie.Movie_BE.Model.*;
import com.movie.Movie_BE.Repository.*;
import com.movie.Movie_BE.dto.FavoriteDTO;
import com.movie.Movie_BE.dto.FilmDTO;
import com.movie.Movie_BE.dto.FilmSummary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    HistoryService historyService;
    @Autowired
    NotificationService notificationService;

    @Autowired
    private ApplicationStateRepository applicationStateRepository;

    private Long previousTotalElements = 0L;

    @PersistenceContext
    private EntityManager entityManager;

    //get lastest Film
    public Page<FilmSummary> getLatestFilms(int page, int size) {
        applicationStateRepository.findTopByOrderByIdDesc()
                .ifPresent(state -> previousTotalElements = state.getPreviousTotalElements());

        Pageable pageable = PageRequest.of(page, size);
        Page<Film> filmPage = filmRepository.findAll(pageable);

        // Chuyển đổi danh sách phim thành FilmSummary
        List<FilmSummary> filmSummaries = filmPage.getContent().stream()
                .map(film -> {
                    FilmSummary summary = new FilmSummary();
                    summary.setId(film.getId());
                    summary.setName(film.getName());
                    summary.setSlug(film.getSlug());
                    summary.setOrigin_name(film.getOrigin_name());
                    summary.setPoster_url(film.getPoster_url());
                    summary.setThumb_url(film.getThumb_url());
                    summary.setView(film.getView());
                    summary.setYear(film.getYear());
                    return summary;
                })
                .collect(Collectors.toList());


        if (previousTotalElements > 0 && filmPage.getTotalElements() > previousTotalElements) {
            Long totalFilmUpdate = filmPage.getTotalElements() - previousTotalElements;
            sendNotificationToUsers(totalFilmUpdate);
        }

        previousTotalElements = filmPage.getTotalElements();
        ApplicationState applicationState = new ApplicationState();
        applicationState.setPreviousTotalElements(previousTotalElements);
        applicationStateRepository.save(applicationState);

        return new PageImpl<>(filmSummaries, pageable, filmPage.getTotalElements());
    }

    @Async
    void sendNotificationToUsers(Long totalFilmUpdate) {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            String message = "Có "+ totalFilmUpdate +" phim mới cập nhật!";
            notificationService.createNotification(user, message, "NEW_FILM_UPDATE", null);
        });
    }


    //Filter film by type
    public Page<FilmSummary> filterFilmsByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Film> filmPage = filmRepository.findByType(type, pageable);

        Page<FilmSummary> filmSummariesPage = filmPage.map(film -> {
            FilmSummary summary = new FilmSummary();
            summary.setId(film.getId());
            summary.setName(film.getName());
            summary.setSlug(film.getSlug());
            summary.setOrigin_name(film.getOrigin_name());
            summary.setPoster_url(film.getPoster_url());
            summary.setThumb_url(film.getThumb_url());
            summary.setView(film.getView());
            summary.setYear(film.getYear());
            return summary;
        });

        return filmSummariesPage;
    }


// sort by view
    public Page<FilmSummary> sortByView(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "view"));

        Page<Film> filmPage = filmRepository.findAll(pageable);

        // Chuyển đổi từ Film sang FilmSummary
        return filmPage.map(film -> {
            FilmSummary summary = new FilmSummary();
            summary.setId(film.getId());
            summary.setName(film.getName());
            summary.setSlug(film.getSlug());
            summary.setOrigin_name(film.getOrigin_name());
            summary.setPoster_url(film.getPoster_url());
            summary.setThumb_url(film.getThumb_url());
            summary.setView(film.getView());
            summary.setYear(film.getYear());
            return summary;
        });
    }




    //find by category
    public Page<FilmSummary> filterFilmsByCategory(String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Tìm phim theo tên category
        Page<Film> filmPage = filmRepository.findByCategories_Name(categoryName, pageable);

        // Chuyển đổi từ Film -> FilmSummary
        return filmPage.map(film -> {
            FilmSummary summary = new FilmSummary();
            summary.setId(film.getId());
            summary.setName(film.getName());
            summary.setSlug(film.getSlug());
            summary.setOrigin_name(film.getOrigin_name());
            summary.setPoster_url(film.getPoster_url());
            summary.setThumb_url(film.getThumb_url());
            summary.setView(film.getView());
            summary.setYear(film.getYear());
            return summary;
        });
    }



    //find by country
    public Page<FilmSummary> filterFilmsByCountry(String countryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Tìm phim theo tên quốc gia
        Page<Film> filmPage = filmRepository.findByCountries_Name(countryName, pageable);

        // Chuyển đổi từ Film -> FilmSummary
        return filmPage.map(film -> {
            FilmSummary summary = new FilmSummary();
            summary.setId(film.getId());
            summary.setName(film.getName());
            summary.setSlug(film.getSlug());
            summary.setOrigin_name(film.getOrigin_name());
            summary.setPoster_url(film.getPoster_url());
            summary.setThumb_url(film.getThumb_url());
            summary.setView(film.getView());
            summary.setYear(film.getYear());
            return summary;
        });
    }





    // get film detail by slug and save watch history of user (if any)
    @Transactional
    public Film getFilmDetailBySlug(String slug, Optional<String> username) {
        Film film = filmRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Film not found with slug: " + slug));

        username.ifPresent(user -> historyService.saveWatchHistory(user, film.getId()));

        // cập nhật view mới
        filmRepository.updateViewWithoutModified(film.getId(), film.getView() + 100);
        return film;
    }




    //create list film
    public List<Film> createFilms(List<FilmDTO> filmDTOs) {
        List<Film> createdFilms = new ArrayList<>();

        for (FilmDTO filmDTO : filmDTOs) {
            if (filmRepository.existsBySlug(filmDTO.getSlug())) {
                throw new IllegalArgumentException("Slug đã tồn tại cho phim: " + filmDTO.getName());
            }

            Film film = new Film();
            film.setName(filmDTO.getName());
            film.setSlug(filmDTO.getSlug());
            film.setOrigin_name(filmDTO.getOrigin_name());
            film.setPoster_url(filmDTO.getPoster_url());
            film.setThumb_url(filmDTO.getThumb_url());
            film.setYear(filmDTO.getYear());
            film.setType(filmDTO.getType());
            film.setContent(filmDTO.getContent());
            film.setStatus(filmDTO.getStatus());
            film.setTime(filmDTO.getTime());
            film.setEpisode_current(filmDTO.getEpisode_current());
            film.setEpisode_total(filmDTO.getEpisode_total());
            film.setView(filmDTO.getView());
            film.setActor(filmDTO.getActor());
            film.setDirector(filmDTO.getDirector());


            if (filmDTO.getCategoryIds() != null) {
                List<Category> categories = categoryRepository.findAllById(filmDTO.getCategoryIds());
                film.setCategories(categories);
            }


            if (filmDTO.getCountryIds() != null) {
                List<Country> countries = countryRepository.findAllById(filmDTO.getCountryIds());
                film.setCountries(countries);
            }


            if (filmDTO.getEpisodes() != null) {
                List<Episode> episodes = filmDTO.getEpisodes().stream()
                        .map(episodeDTO -> {
                            Episode episode = new Episode();
                            episode.setId(episodeDTO.getId());
                            episode.setName(episodeDTO.getName());
                            episode.setSlug(episodeDTO.getSlug());
                            episode.setFilename(episodeDTO.getFilename());
                            episode.setLink_embed(episodeDTO.getLink_embed());
                            episode.setLink_m3u8(episodeDTO.getLink_m3u8());
                            episode.setFilm(film);
                            return episode;
                        })
                        .collect(Collectors.toList());
                film.setEpisodes(episodes);
            }

            Film savedFilm = filmRepository.save(film);
            createdFilms.add(savedFilm);
        }

        return createdFilms;
    }



    //update film
    public Film updateFilm(Long id, Film filmDetails) {
        // Tìm phim theo ID
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film not found with id: " + id));

        // Cập nhật các thông tin cơ bản nếu có
        updateBasicInfo(film, filmDetails);

        // Cập nhật danh mục và quốc gia nếu có
        updateCategoriesAndCountries(film, filmDetails);

        return filmRepository.save(film);
    }

    private void updateBasicInfo(Film film, Film filmDetails) {
        if (filmDetails.getName() != null) {
            film.setName(filmDetails.getName());
        }
        if (filmDetails.getSlug() != null && !filmDetails.getSlug().equals(film.getSlug())) {
            if (filmRepository.existsBySlug(filmDetails.getSlug())) {
                throw new IllegalArgumentException("Slug đã tồn tại: " + filmDetails.getSlug());
            }
            film.setSlug(filmDetails.getSlug());
        }
        if (filmDetails.getOrigin_name() != null) {
            film.setOrigin_name(filmDetails.getOrigin_name());
        }
        if (filmDetails.getPoster_url() != null) {
            film.setPoster_url(filmDetails.getPoster_url());
        }
        if (filmDetails.getThumb_url() != null) {
            film.setThumb_url(filmDetails.getThumb_url());
        }
        if (filmDetails.getYear() != 0) {
            film.setYear(filmDetails.getYear());
        }
        if (filmDetails.getContent() != null) {
            film.setContent(filmDetails.getContent());
        }
        if (filmDetails.getStatus() != null) {
            film.setStatus(filmDetails.getStatus());
        }
        if (filmDetails.getTime() != null) {
            film.setTime(filmDetails.getTime());
        }
        if (filmDetails.getType() != null) {
            film.setType(filmDetails.getType());
        }
        if (filmDetails.getEpisode_current() != null) {
            film.setEpisode_current(filmDetails.getEpisode_current());
        }
        if (filmDetails.getEpisode_total() != null) {
            film.setEpisode_total(filmDetails.getEpisode_total());
        }
        if (filmDetails.getView() != 0) {
            film.setView(filmDetails.getView());
        }
        if (filmDetails.getActor() != null) {
            film.setActor(filmDetails.getActor());
        }
        if (filmDetails.getDirector() != null) {
            film.setDirector(filmDetails.getDirector());
        }
    }

    private void updateCategoriesAndCountries(Film film, Film filmDetails) {
        // Cập nhật danh mục và quốc gia
        if (filmDetails.getCategories() != null && !filmDetails.getCategories().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(
                    filmDetails.getCategories().stream().map(Category::getId).collect(Collectors.toList())
            );
            film.setCategories(categories);
        }

        if (filmDetails.getCountries() != null && !filmDetails.getCountries().isEmpty()) {
            List<Country> countries = countryRepository.findAllById(
                    filmDetails.getCountries().stream().map(Country::getId).collect(Collectors.toList())
            );
            film.setCountries(countries);
        }
    }
    //end update film



    //delete film
    public void deleteFilm(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film not found with id: " + id));
        filmRepository.delete(film);
    }


    //add film to favorite
    public Favorite addFilmToFavorites(FavoriteDTO favoriteDTO) {

        Film film = filmRepository.findBySlug(favoriteDTO.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("Film not found"));


        User user = userRepository.findByUserName(favoriteDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        Optional<Favorite> existingFavorite = favoriteRepository.findByFilmAndUser(film, user);
        if (existingFavorite.isPresent()) {
            throw new RuntimeException("Phim đã có trong danh sách yêu thích của bạn");
        }


        Favorite favorite = new Favorite();
        favorite.setUsername(favoriteDTO.getUsername());
        favorite.setFilm(film);
        favorite.setUser(user);
        favorite.setCreatedAt(LocalDateTime.now());

        return favoriteRepository.save(favorite);

    }


//    remove film from favorite
    public void removeFilmFromFavorites(String userName, String slug) {

        Film film = filmRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Film not found"));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Favorite> existingFavorite = favoriteRepository.findByFilmAndUser(film, user);
        if (existingFavorite.isEmpty()) {
            throw new IllegalArgumentException("Phim không có trong danh sách yêu thích của bạn");
        }

        favoriteRepository.delete(existingFavorite.get());
    }





    // get favorite film by UserName
    public Page<FilmSummary> getFavoritesByUser(String username, int page, int size) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Favorite> favoritesPage = favoriteRepository.findByUser(user, pageable);

        // Chuyển đổi từ Favorite -> Film -> FilmSummary
        Page<FilmSummary> filmSummariesPage = favoritesPage.map(favorite -> {
            Film film = favorite.getFilm();
            FilmSummary filmSummary = new FilmSummary();
            filmSummary.setId(film.getId());
            filmSummary.setName(film.getName());
            filmSummary.setSlug(film.getSlug());
            filmSummary.setOrigin_name(film.getOrigin_name());
            filmSummary.setPoster_url(film.getPoster_url());
            filmSummary.setThumb_url(film.getThumb_url());
            filmSummary.setView(film.getView());
            filmSummary.setYear(film.getYear());
            return filmSummary;
        });

        return filmSummariesPage;
    }




    //search film and sort by newest time
    public List<FilmSummary> searchFilmsByKeywords(String keyword) {
        String[] keywords = keyword.trim().toLowerCase().split("\\s+");

        StringBuilder queryBuilder = new StringBuilder("SELECT f FROM Film f WHERE ");
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                queryBuilder.append(" OR ");
            }
            queryBuilder.append("(")
                    .append("LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword" + i + ", '%')) OR ")
                    .append("LOWER(f.origin_name) LIKE LOWER(CONCAT('%', :keyword" + i + ", '%')) OR ")
                    .append("LOWER(REPLACE(f.slug, '-', ' ')) LIKE LOWER(CONCAT('%', :keyword" + i + ", '%'))")
                    .append(")");
        }

        TypedQuery<Film> query = entityManager.createQuery(queryBuilder.toString(), Film.class);
        for (int i = 0; i < keywords.length; i++) {
            query.setParameter("keyword" + i, keywords[i]);
        }

        List<Film> films = query.getResultList();

        films.sort((f1, f2) -> {
            int yearComparison = Integer.compare(f2.getYear(), f1.getYear());
            if (yearComparison != 0) {
                return yearComparison;
            }
            return Integer.compare(f2.getView(), f1.getView());
        });


        List<FilmSummary> filmSummaries = films.stream().map(film -> {
            FilmSummary summary = new FilmSummary();
            summary.setId(film.getId());
            summary.setName(film.getName());
            summary.setSlug(film.getSlug());
            summary.setOrigin_name(film.getOrigin_name());
            summary.setPoster_url(film.getPoster_url());
            summary.setThumb_url(film.getThumb_url());
            summary.setView(film.getView());
            summary.setYear(film.getYear());
            return summary;
        }).collect(Collectors.toList());

        return filmSummaries;
    }
}
