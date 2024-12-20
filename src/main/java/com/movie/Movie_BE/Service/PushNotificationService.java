package com.movie.Movie_BE.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PushNotificationService {
    @Autowired
    private FirebaseMessaging firebaseMessaging;

    // Phương thức gửi thông báo push
    public void sendPushNotification(String token, String title, String body) throws Exception {
        // Tạo thông báo FCM
        Notification notification = Notification.builder()
                .setTitle(title)  // Tiêu đề thông báo
                .setBody(body)    // Nội dung thông báo
                .build();
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);

        // Tạo thông điệp FCM
        Message message = Message.builder()
                .setToken(token)  // Token của thiết bị người nhận
                .putAllData(data)  // Thông báo
                .setNotification(notification)  // Thông báo
                .build();

        // Gửi thông báo
        String response = firebaseMessaging.send(message);
        System.out.println("Thông báo đã được gửi thành công: " + response);
    }
}