package com.movie.Movie_BE.Service;

import com.movie.Movie_BE.Model.User;
import com.movie.Movie_BE.Repository.UserRepository;
import com.movie.Movie_BE.dto.CommentDTO;
import com.movie.Movie_BE.Model.Comment;
import com.movie.Movie_BE.Model.Film;
import com.movie.Movie_BE.Repository.CommentRepository;
import com.movie.Movie_BE.Repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;


    public Comment saveComment(CommentDTO commentDTO) {
        // Tìm film dựa trên slug
        Film film = filmRepository.findBySlug(commentDTO.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("Film not found with slug: " + commentDTO.getSlug()));

        // Tìm user dựa trên userId
        User user = userRepository.findByUid(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + commentDTO.getUserId()));

        // Tạo mới comment
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setFilm(film);
        comment.setUser(user); // Thiết lập quan hệ với User
        comment = commentRepository.save(comment);

        // Lấy danh sách các bình luận liên quan đến film
        List<Comment> filmComments = film.getComments();

        // Lấy danh sách các tên người dùng khác đã bình luận
        List<String> otherUserNames = filmComments.stream()
                .map(c -> c.getUser().getUserName()) // Lấy tên từ quan hệ User
                .filter(userName -> !userName.equals(user.getUserName())) // So sánh với tên người dùng hiện tại
                .distinct()
                .collect(Collectors.toList());

        // Gửi thông báo đến các người dùng khác
        String message = "Người dùng: " + user.getUserName() + " cũng đã bình luận về phim " + film.getName();
        notificationService.sendNotificationToUsers(otherUserNames, message, film.getId());

        return comment;
    }




    public Comment updateComment(Long id, String userName, String newContent) {
        // Tìm comment
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Kiểm tra quyền sở hữu dựa trên userName
        if (!comment.getUser().getUserName().equals(userName)) {
            throw new RuntimeException("You can only update your own comment!");
        }

        // Cập nhật nội dung mới nếu có
        if (newContent != null && !newContent.trim().isEmpty()) {
            comment.setContent(newContent);
            comment.setCreatedAt(LocalDateTime.now());
        }

        // Lưu lại comment
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id, String userName) {
        // Tìm comment
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Kiểm tra quyền sở hữu
        if (!comment.getUser().getUserName().equals(userName)) {
            throw new RuntimeException("You can only delete your own comment!");
        }

        // Xóa comment
        commentRepository.delete(comment);
    }


}
