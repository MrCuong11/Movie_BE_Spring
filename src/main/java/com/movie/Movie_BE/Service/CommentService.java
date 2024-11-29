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

        Film film = filmRepository.findBySlug(commentDTO.getSlug())
                .orElseThrow(() -> new RuntimeException("Film not found"));


        User user = userRepository.findByUserName(commentDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));


        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUsername(commentDTO.getUsername());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setFilm(film);
        comment.setUser(user);
        comment = commentRepository.save(comment);

        List<Comment> filmComments = film.getComments();

        List<String> otherUserNames = filmComments.stream()
                .map(Comment::getUsername)
                .filter(userName -> !userName.equals(commentDTO.getUsername()))
                .distinct()
                .collect(Collectors.toList());


        String message = "Người dùng: " + commentDTO.getUsername() + " cũng đã bình luận về phim " + film.getName();
        notificationService.sendNotificationToUsers(otherUserNames, message, film.getId());
        return comment;
    }


    public Comment updateComment (Long id, String userName, String newContent){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        //check if user is the owner of the comment
        if(!comment.getUsername().equals(userName)){
            throw new RuntimeException("You can only update your own comment !");
        }

        if(newContent != null){
            comment.setContent(newContent);
            comment.setCreatedAt(LocalDateTime.now());
        }

        return commentRepository.save(comment);
    }

    public void deleteComment (Long id, String userName){
        Comment comment = commentRepository.findById(id)
                .orElseThrow( ()->new RuntimeException("Comment not found"));
        if(!comment.getUsername().equals(userName)){
            throw new RuntimeException("You can only delete your own comment");
        }

        commentRepository.delete(comment);
    }

}
