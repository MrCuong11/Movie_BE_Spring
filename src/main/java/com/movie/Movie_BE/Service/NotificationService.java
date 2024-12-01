package com.movie.Movie_BE.Service;

import com.movie.Movie_BE.Model.Notification;
import com.movie.Movie_BE.Model.Token;
import com.movie.Movie_BE.Model.User;
import com.movie.Movie_BE.Repository.NotificationRepository;
import com.movie.Movie_BE.Repository.TokenRepository;
import com.movie.Movie_BE.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;
    private final TokenRepository tokenRepository;
    public void createNotification(User user, String message, String actionType, Long relatedId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setActionType(actionType);
        notification.setRelatedId(relatedId);
        notificationRepository.save(notification);
    }

    // Lấy tất cả thông báo chưa đọc của user
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUser_UidAndIsReadFalse(userId);
    }

    // Đánh dấu thông báo là đã đọc
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // Xóa thông báo theo notificationId
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.delete(notification);
    }

    @Async
    public void sendNotificationToUsers(List<String> otherUserNames, String message, Long filmId) {
        List<User> otherUsers = userRepository.findByUserNameIn(otherUserNames);
        otherUsers.forEach(user -> {
            List<Token> userTokens = tokenRepository.findByUser(user);
            if (userTokens != null && !userTokens.isEmpty()) {
                String title = "Bình luận mới";
                String body = message;

                userTokens.forEach(token ->{
                            if (token.getToken() != null && !token.getToken().isEmpty()) {
                                try {
                                    pushNotificationService.sendPushNotification(token.getToken(), title, body);
                                } catch (Exception e) {
                                    System.err.println("Lỗi khi gửi thông báo đến token: " + token.getToken());
                                    e.printStackTrace();
                                }
                            }
                });
            }
            createNotification(user, message, "ADD_COMMENT", filmId);
        });
    }

}
//    chưa xong
