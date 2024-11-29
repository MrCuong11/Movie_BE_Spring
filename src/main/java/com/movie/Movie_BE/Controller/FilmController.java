package com.movie.Movie_BE.Controller;

import com.movie.Movie_BE.Model.Favorite;
import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Service.FilmService;
import com.movie.Movie_BE.dto.FavoriteDTO;
import com.movie.Movie_BE.dto.FilmDTO;
import com.movie.Movie_BE.dto.FilmSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.movie.Movie_BE.Utils.ResponseUtils.createResponse;

@RestController
@RequestMapping("/danh-sach")
public class FilmController {
    @Autowired
    private FilmService filmService;

    @Operation(
            summary = "Lấy danh sách phim mới cập nhật",
            description = "Trả về danh sách các phim mới nhất, giới hạn theo số trang và số lượng mỗi trang."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về danh sách phim mới nhất"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @GetMapping("/phim-moi-cap-nhat")
    public ResponseEntity<Object> getLatestFilms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<FilmSummary> films = filmService.getLatestFilms(page - 1, pageSize);
        return ResponseEntity.ok(createResponse(films));
    }



    @Operation(
            summary = "Trả về danh sách phim theo type",
            description = "Trả về danh sách các phim theo type, giới hạn theo số trang và số lượng mỗi trang."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @GetMapping("/filterByType/{type}")
    public ResponseEntity<Object> filterFilmsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<FilmSummary> films = filmService.filterFilmsByType(type, page - 1, pageSize);
        return ResponseEntity.ok(createResponse(films));
    }





    @Operation(
            summary = "Trả về danh sách phim theo view giảm dần",
            description = "Trả về danh sách các phim theo view giảm dần, giới hạn theo số trang và số lượng mỗi trang."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @GetMapping("/sortByView")
    public ResponseEntity<Object> sortFilm(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<FilmSummary> films = filmService.sortByView(page - 1, pageSize);
        return ResponseEntity.ok(createResponse(films));
    }




    // Lọc phim theo thể loại và trả về kết quả phân trang
    @Operation(summary = "Lọc phim theo thể loại", description = "Trả về danh sách phim được phân trang theo thể loại.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách phim theo thể loại",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thể loại phim")
    })
    @GetMapping("/filterByCategory/{category}")
    public ResponseEntity<Object> filterFilmsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Gọi service để lấy danh sách phim theo category
        Page<FilmSummary> filmSummariesPage = filmService.filterFilmsByCategory(category, page - 1, size);

        // Trả về response với thông tin phim đã phân trang
        return ResponseEntity.ok(createResponse(filmSummariesPage));
    }

    // Lọc phim theo quốc gia và trả về kết quả phân trang
    @Operation(summary = "Lọc phim theo quốc gia", description = "Trả về danh sách phim được phân trang theo quốc gia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách phim theo quốc gia",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy quốc gia phim")
    })
    @GetMapping("/filterByCountry/{country}")
    public ResponseEntity<Object> filterFilmsByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<FilmSummary> filmSummariesPage = filmService.filterFilmsByCountry(country, page - 1, size);
        return ResponseEntity.ok(createResponse(filmSummariesPage));
    }




    @Operation(
            summary = "Lấy chi tiết phim theo slug và lưu lịch sử xem của người dùng (nếu có người dùng)",
            description = "Trả về thông tin chi tiết của một phim dựa trên slug đã cung cấp."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về thông tin chi tiết phim"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phim với slug đã cung cấp"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @GetMapping("/phim/{slug}")
    public ResponseEntity<Film> getFilmDetailBySlug(@PathVariable String slug, @RequestParam Optional<String> username) {
        Film film = filmService.getFilmDetailBySlug(slug, username);
        return ResponseEntity.ok(film);
    }




    @Operation(
            summary = "Tạo list phim mới",
            description = "Tạo một list phim mới với các thông tin chi tiết được cung cấp trong request body. (nhớ phải thêm dấu [ ] ở đầu giữa các phim)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Phim được tạo thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Film.class))),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ (dữ liệu không hợp lệ hoặc thiếu thông tin)"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @PostMapping("/phim")
    public ResponseEntity<List<Film>> createFilms(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Danh sách các phim mới cần tạo (vì là list phim nên thêm hộ [ ] ở đằng trước)",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilmDTO.class))
            )
            @RequestBody List<FilmDTO> films
    )
    {
        List<Film> createdFilms = filmService.createFilms(films);
        return ResponseEntity.status(201).body(createdFilms);
    }



    @Operation(
            summary = "Cập nhật thông tin phim",
            description = "Cập nhật thông tin của một phim cụ thể dựa trên ID của phim ( cần cập nhật trường nào thì truyền vào body trường đó thôi)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phim được cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Film.class))),
            @ApiResponse(responseCode = "404", description = "Phim không tồn tại với ID đã cung cấp"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @PutMapping("/phim/{id}")
    public ResponseEntity<Film> updateFilm(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin chi tiết của phim cần cập nhật",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Film.class))
            )
            @RequestBody Film film
    ) {
        Film updatedFilm = filmService.updateFilm(id, film);
        return ResponseEntity.ok(updatedFilm);
    }











    @Operation(
            summary = "Xóa phim theo ID",
            description = "Xóa phim khỏi hệ thống dựa trên ID được cung cấp."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa phim thành công, không có nội dung trả về"),
            @ApiResponse(responseCode = "404", description = "Phim không tồn tại với ID đã cung cấp"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @DeleteMapping("/phim/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Tìm kiếm phim theo từ khóa"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách phim phù hợp với từ khóa tìm kiếm"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ, từ khóa trống hoặc không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
    })
    @GetMapping("/search")
    public ResponseEntity<List<FilmSummary>> searchFilms(
            @RequestParam String keyword
    ) {
        List<FilmSummary> films = filmService.searchFilmsByKeywords(keyword);
        return ResponseEntity.ok(films);
    }


    @Operation(summary = "Thêm phim vào danh sách yêu thích", description = "Cho phép người dùng thêm một phim vào danh sách yêu thích của họ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Phim đã được thêm vào danh sách yêu thích",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Favorite.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    @PostMapping("/addFavorite")
    public ResponseEntity<Favorite> addFilmToFavorites(
            @RequestBody FavoriteDTO favoriteDTO) {
        Favorite newFavorite = filmService.addFilmToFavorites(favoriteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newFavorite);
    }

    @Operation(summary = "Xóa phim khỏi danh sách yêu thích", description = "Cho phép người dùng xóa một phim khỏi danh sách yêu thích của họ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phim đã được xóa khỏi danh sách yêu thích",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phim trong danh sách yêu thích")
    })
    @DeleteMapping("/removeFavorite/{username}/{slug}")
    public ResponseEntity<String> removeFilmFromFavorites(
            @PathVariable String username,
            @PathVariable String slug) {
        try {
            filmService.removeFilmFromFavorites(username, slug);
            return ResponseEntity.ok("Phim đã được xóa khỏi danh sách yêu thích của bạn");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }





    @Operation(summary = "Lấy danh sách phim yêu thích của người dùng", description = "Trả về danh sách phim yêu thích của người dùng theo trang.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách phim yêu thích của người dùng",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    @GetMapping("/favorite/{username}")
    public ResponseEntity<Object> getFavoritesByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<FilmSummary> filmSummariesPage = filmService.getFavoritesByUser(username, page - 1, size);
        return ResponseEntity.ok(createResponse(filmSummariesPage));
    }


}
