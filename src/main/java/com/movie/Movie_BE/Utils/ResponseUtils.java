package com.movie.Movie_BE.Utils;

import com.movie.Movie_BE.dto.FilmSummary;
import com.movie.Movie_BE.dto.Pagination;
import org.springframework.data.domain.Page;

import java.util.List;

public class ResponseUtils {

    // Hàm tạo response với phân trang và danh sách phim
    public static <T> Object createResponse(Page<T> page) {
        return new Object() {
            public boolean status = page.hasContent();
            public List<T> items = page.getContent();
            public Pagination pagination = new Pagination(page);
        };
    }

}
