package com.movie.Movie_BE.Controller;

import com.movie.Movie_BE.Model.Notification;
import com.movie.Movie_BE.Model.TokenRequest;
import com.movie.Movie_BE.Service.NotificationService;
import com.movie.Movie_BE.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;



    @Operation(summary = "Lấy tất cả thông báo chưa đọc của user", description = "Trả về danh sách các thông báo chưa đọc cho người dùng với ID userId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông báo thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "404", description = "User không tìm thấy")
    })
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@Parameter(description = "ID của user cần lấy thông báo") @PathVariable String userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }







    @Operation(summary = "Đánh dấu thông báo là đã đọc", description = "Đánh dấu thông báo là đã đọc dựa trên notificationId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đánh dấu thành công thông báo là đã đọc"),
            @ApiResponse(responseCode = "404", description = "Thông báo không tìm thấy")
    })
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<String> markNotificationAsRead(@Parameter(description = "ID của thông báo cần đánh dấu là đã đọc") @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read successfully.");
    }







    @Operation(summary = "Xóa thông báo", description = "Xóa thông báo theo notificationId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa thông báo thành công"),
            @ApiResponse(responseCode = "404", description = "Thông báo không tìm thấy")
    })
    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<String> deleteNotification(@Parameter(description = "ID của thông báo cần xóa") @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Xóa thành công");
    }









    @PostMapping("/saveToken")
    public ResponseEntity<String> saveFcmToken(@RequestBody TokenRequest tokenRequest) {
        try {
            userService.saveFcmToken(tokenRequest.getUsername(), tokenRequest.getFcmToken());
            return ResponseEntity.ok("Token saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save token");
        }
    }

    @Operation(summary = "Cập nhật FCM Token cho người dùng", description = "Cập nhật FCM token mới khi người dùng đăng nhập hoặc token thay đổi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật token thành công"),
            @ApiResponse(responseCode = "400", description = "Cập nhật token thất bại")
    })
    @PutMapping("/updateToken")
    public ResponseEntity<String> updateFcmToken(@RequestBody TokenRequest tokenRequest) {
        try {
            userService.saveFcmToken(tokenRequest.getUsername(), tokenRequest.getFcmToken());
            return ResponseEntity.ok("Token updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update token: " + e.getMessage());
        }
    }
}
//    chưa xong