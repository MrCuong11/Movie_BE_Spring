package com.movie.Movie_BE.Service;


import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Model.History;
import com.movie.Movie_BE.Model.User;
import com.movie.Movie_BE.Repository.FilmRepository;
import com.movie.Movie_BE.Repository.HistoryRepository;
import com.movie.Movie_BE.Repository.UserRepository;
import com.movie.Movie_BE.dto.HistorySummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    public void saveWatchHistory(String username, Long filmId) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film not found"));

        Optional<History> existingHistory = historyRepository.findByUserAndFilm(user, film);

        if (existingHistory.isPresent()) {
            History history = existingHistory.get();
            history.setWatchTime(LocalDateTime.now());
            historyRepository.save(history);
        } else {

            History history = new History();
            history.setUser(user);
            history.setFilm(film);
            history.setWatchTime(LocalDateTime.now());
            historyRepository.save(history);
        }
    }





    // Lấy lịch sử xem phim của người dùng với phân trang
    public Page<HistorySummary> getHistoryByUser(String username, int page, int size) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<History> historyPage = historyRepository.findByUser(user, pageable);

        List<HistorySummary> historySummaries = historyPage.getContent().stream()
                .map(history -> {
                    Film film = history.getFilm();
                    HistorySummary historySummary = new HistorySummary();
                    historySummary.setId(film.getId());
                    historySummary.setName(film.getName());
                    historySummary.setSlug(film.getSlug());
                    historySummary.setOrigin_name(film.getOrigin_name());
                    historySummary.setPoster_url(film.getPoster_url());
                    historySummary.setThumb_url(film.getThumb_url());
                    historySummary.setYear(film.getYear());
                    historySummary.setWatchTime(history.getWatchTime());
                    return historySummary;
                })
                .collect(Collectors.toList());

        historySummaries.sort((f1, f2) -> {
            LocalDateTime modifiedTime1 = f1.getWatchTime();
            LocalDateTime modifiedTime2 = f2.getWatchTime();
            return modifiedTime2.compareTo(modifiedTime1);
        });
        return new PageImpl<>(historySummaries, pageable, historySummaries.size());
    }


    public void removeHistoryByFilm(String username, Long filmId) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film not found"));

        History history = historyRepository.findByUserAndFilm(user, film)
                .orElseThrow(() -> new IllegalArgumentException("Watch history not found"));

        historyRepository.delete(history);
    }

    public void removeAllHistoryByUser(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<History> histories = historyRepository.findByUser(user);
        if (histories.isEmpty()) {
            throw new IllegalArgumentException("No watch history found for this user");
        }

        historyRepository.deleteAll(histories);
    }





}
