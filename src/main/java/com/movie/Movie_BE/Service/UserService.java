package com.movie.Movie_BE.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.movie.Movie_BE.Model.Token;
import com.movie.Movie_BE.Model.User;
import com.movie.Movie_BE.Repository.TokenRepository;
import com.movie.Movie_BE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private FirebaseUserService firebaseUserService;

    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateKeyException("Email đã tồn tại: " + user.getEmail());
        }

        return userRepository.save(user);
    }


    // Sửa thông tin người dùng
    public User updateUser(String uid, String newEmail, String newUsername) {
        Optional<User> optionalUser = userRepository.findByUid(uid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(newEmail);
            user.setUserName(newUsername);

            // Lưu vào database
            User updatedUser = userRepository.save(user);

            // Đồng bộ Firebase
            firebaseUserService.updateUserInFirebase(uid, newEmail, newUsername);

            return updatedUser;
        } else {
            throw new RuntimeException("User not found with UID: " + uid);
        }
    }

    // Xóa người dùng
    public void deleteUser(String uid) {
        Optional<User> optionalUser = userRepository.findByUid(uid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Xóa khỏi database
            userRepository.delete(user);

            // Đồng bộ Firebase
            firebaseUserService.deleteUserFromFirebase(uid);
        } else {
            throw new RuntimeException("User not found with UID: " + uid);
        }
    }

    // Lấy tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();
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