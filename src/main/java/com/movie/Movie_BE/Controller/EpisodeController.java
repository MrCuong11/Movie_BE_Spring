package com.movie.Movie_BE.Controller;

import com.movie.Movie_BE.Model.Episode;
import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Service.EpisodeService;
import io.swagger.v3.oas.annotations.Operation;  // Import annotation Swagger
import io.swagger.v3.oas.annotations.Parameter;  // Import annotation Swagger
import io.swagger.v3.oas.annotations.responses.ApiResponse;  // Import annotation Swagger
import io.swagger.v3.oas.annotations.responses.ApiResponses;  // Import annotation Swagger
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/films")
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    @Operation(
            summary = "Thêm hoặc cập nhật tập phim cho một bộ phim",
            description = "Điểm cuối này cho phép thêm hoặc cập nhật các tập phim cho một bộ phim cụ thể thông qua filmId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật phim thành công với các tập phim mới"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    @PostMapping("/{filmId}/episodes")
    public ResponseEntity<?> addOrUpdateEpisodes(
            @Parameter(description = "ID của bộ phim để cập nhật các tập phim", required = true)
            @PathVariable Long filmId,  // Nhận filmId từ đường dẫn URL

            @Parameter(description = "Danh sách các tập phim để thêm hoặc cập nhật", required = true)
            @RequestBody List<Episode> episodes) {  // Nhận danh sách các tập phim từ body của yêu cầu
        Film updatedFilm = episodeService.addOrUpdateEpisodes(filmId, episodes);
        return ResponseEntity.ok(updatedFilm);
    }

    @Operation(
            summary = "Xóa một tập phim",
            description = "Điểm cuối này cho phép xóa một tập phim cụ thể của bộ phim thông qua cả filmId và episodeId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa tập phim thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bộ phim hoặc tập phim")
    })
    @DeleteMapping("/{filmId}/episodes/{episodeId}")
    public ResponseEntity<?> deleteEpisode(
            @Parameter(description = "ID của bộ phim để xóa tập phim", required = true)
            @PathVariable Long filmId,  // Nhận filmId từ đường dẫn URL

            @Parameter(description = "ID của tập phim cần xóa", required = true)
            @PathVariable Long episodeId) {  // Nhận episodeId từ đường dẫn URL
        episodeService.deleteEpisode(filmId, episodeId);
        return ResponseEntity.ok("Tập phim đã được xóa thành công!");
    }

    @Operation(
            summary = "Lấy tất cả các tập phim của một bộ phim",
            description = "Điểm cuối này trả về tất cả các tập phim của một bộ phim cụ thể thông qua filmId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách các tập phim đã được lấy thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bộ phim")
    })
    @GetMapping("/{slug}/episodes")
    public ResponseEntity<List<Episode>> getAllEpisodes(@PathVariable String slug) {
        List<Episode> episodes = episodeService.getAllEpisodes(slug);
        return ResponseEntity.ok(episodes);
    }
}
