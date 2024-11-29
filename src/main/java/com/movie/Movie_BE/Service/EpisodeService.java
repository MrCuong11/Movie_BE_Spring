package com.movie.Movie_BE.Service;

import com.movie.Movie_BE.Model.Episode;
import com.movie.Movie_BE.Model.Favorite;
import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Model.User;
import com.movie.Movie_BE.Repository.EpisodeRepository;
import com.movie.Movie_BE.Repository.FilmRepository;
import com.movie.Movie_BE.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EpisodeService {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Film addOrUpdateEpisodes(Long filmId, List<Episode> episodes) {
        // Lấy phim từ database
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film id not found"));

        // Lấy danh sách tập phim hiện tại của phim
        List<Episode> existingEpisodes = film.getEpisodes() != null ? film.getEpisodes() : new ArrayList<>();
        Map<String, Episode> existingEpisodeMap = existingEpisodes.stream()
                .collect(Collectors.toMap(Episode::getSlug, episode -> episode));

        for (Episode newEpisode : episodes) {
            if (newEpisode.getSlug() != null && existingEpisodeMap.containsKey(newEpisode.getSlug())) {
                // Cập nhật tập phim cũ
                Episode existingEpisode = existingEpisodeMap.get(newEpisode.getSlug());
                existingEpisode.setName(newEpisode.getName());
                existingEpisode.setFilename(newEpisode.getFilename());
                existingEpisode.setLink_embed(newEpisode.getLink_embed());
                existingEpisode.setLink_m3u8(newEpisode.getLink_m3u8());
            } else {
                // Thêm tập phim mới
                newEpisode.setFilm(film);
                existingEpisodes.add(newEpisode);
                sendNotificationToUsers(film, newEpisode);
            }
        }


        // Cập nhật danh sách tập phim và lưu phim
        film.setEpisodes(existingEpisodes);
        return filmRepository.save(film);
    }

    /**
     * Xóa một tập phim của một bộ phim
     */
    @Transactional
    public void deleteEpisode(Long filmId, Long episodeId) {
        // Lấy phim từ database
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film id not found"));

        // Lấy tập phim cần xóa
        Episode episodeToRemove = film.getEpisodes().stream()
                .filter(episode -> episode.getId().equals(episodeId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Film id not found"));

        // Xóa tập phim
        film.getEpisodes().remove(episodeToRemove);
        episodeRepository.delete(episodeToRemove); // Xóa khỏi DB
        filmRepository.save(film); // Lưu lại danh sách tập phim mới
    }


    @Async
    public void sendNotificationToUsers(Film film, Episode episode) {
        List<Favorite> favoriteFilm = film.getFavorites();
        List<String> userFavoriteFilms = favoriteFilm.stream()
                .map(Favorite::getUsername)
                .distinct()
                .collect(Collectors.toList());

        // Lấy người dùng từ danh sách yêu thích
        List<User> users = userRepository.findAllByUserNameIn(userFavoriteFilms);

        String message = String.format("Phim %s vừa có tập mới: %s!", film.getName(), episode.getName());
        for (User user : users) {
            notificationService.createNotification(user, message, "NEW_EPISODE", film.getId());
        }
    }



    @Transactional
    public List<Episode> getAllEpisodes(String slug) {
        List<Episode> episodes = episodeRepository.findByFilmSlug(slug)
                .orElseThrow(() -> new RuntimeException("Film id not found"));

        // Trả về danh sách tập phim
        return episodes;
    }
}

