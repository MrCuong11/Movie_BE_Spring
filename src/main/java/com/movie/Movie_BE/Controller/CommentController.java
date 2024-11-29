package com.movie.Movie_BE.Controller;

import com.movie.Movie_BE.Model.Comment;
import com.movie.Movie_BE.Service.CommentService;
import com.movie.Movie_BE.dto.CommentDTO;
import com.movie.Movie_BE.dto.UpdateCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Thêm bình luận mới
    @Operation(summary = "Thêm bình luận mới", description = "Thêm một bình luận mới cho một phim.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bình luận đã được thêm thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO commentDTO) {
        Comment newComment = commentService.saveComment(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    // Cập nhật bình luận
    @Operation(summary = "Cập nhật bình luận", description = "Cập nhật nội dung của một bình luận dựa trên ID bình luận.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bình luận đã được cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bình luận với ID đã cho"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id,
                                                 @RequestBody UpdateCommentDTO updateCommentDTO){
        Comment updatedComment = commentService.updateComment(id, updateCommentDTO.getUsername(), updateCommentDTO.getNewContent());
        return ResponseEntity.ok(updatedComment);
    }

    // Xóa bình luận
    @Operation(summary = "Xóa bình luận", description = "Xóa một bình luận dựa trên ID bình luận và tên người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bình luận đã được xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bình luận với ID đã cho hoặc người dùng không có quyền xóa")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @RequestParam String username) {
        commentService.deleteComment(id, username);
        return ResponseEntity.noContent().build();
    }
}