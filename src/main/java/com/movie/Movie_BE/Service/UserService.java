package com.movie.Movie_BE.Service;

import com.movie.Movie_BE.Model.Token;
import com.movie.Movie_BE.Model.User;
import com.movie.Movie_BE.Repository.TokenRepository;
import com.movie.Movie_BE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateKeyException("Email đã tồn tại: " + user.getEmail());
        }

        return userRepository.save(user);
    }


    public void saveFcmToken(String username, String fcmToken) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(fcmToken == null && fcmToken.isEmpty()){
            throw new IllegalArgumentException("FCM token doesn't valid");
        }

        Token existingToken = tokenRepository.findByToken(fcmToken);
        if(existingToken != null){
            if(!existingToken.getUser().getUid().equals(user.getUid())){
                throw new IllegalArgumentException("FCM token already used by other User");
            }

            existingToken.setCreatedAt(java.time.LocalDateTime.now());
            tokenRepository.save(existingToken);
        }else{
            Token newToken = new Token();
            newToken.setToken(fcmToken);
            newToken.setUser(user);
            newToken.setCreatedAt(java.time.LocalDateTime.now());
            tokenRepository.save(newToken);
        }
    }
}
//chưa xong