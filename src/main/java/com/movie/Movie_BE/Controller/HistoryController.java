package com.movie.Movie_BE.Controller;

import com.movie.Movie_BE.Model.History;
import com.movie.Movie_BE.Service.HistoryService;
import com.movie.Movie_BE.dto.FilmSummary;
import com.movie.Movie_BE.dto.HistorySummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.movie.Movie_BE.Utils.ResponseUtils.createResponse;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @Operation(summary = "Lấy lịch sử xem phim của người dùng", description = "Trả về danh sách lịch sử xem phim của người dùng theo trang.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách lịch sử xem phim của người dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistorySummary.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    @GetMapping("{username}")
    public ResponseEntity<Object> getHistoryByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<HistorySummary> historySummaries = historyService.getHistoryByUser(username, page - 1, size);
        return ResponseEntity.ok(createResponse(historySummaries));
    }


    @DeleteMapping("/{username}/{filmId}")
    public ResponseEntity<String> removeHistory(@PathVariable String username, @PathVariable Long filmId) {
        try {
            historyService.removeHistoryByFilm(username, filmId);
            return ResponseEntity.ok("History removed successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> removeAllHistory(@PathVariable String username) {
        try {
            historyService.removeAllHistoryByUser(username);
            return ResponseEntity.ok("All history removed successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
