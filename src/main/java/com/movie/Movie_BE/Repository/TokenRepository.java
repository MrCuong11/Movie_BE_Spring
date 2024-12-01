package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.Token;
import com.movie.Movie_BE.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user); // Lấy danh sách token theo User

    Token findByToken(String token); // Kiểm tra token đã tồn tại chưa
}
