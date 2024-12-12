package com.movie.Movie_BE.Controller;

import com.movie.Movie_BE.Model.User;
import com.movie.Movie_BE.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Đăng ký người dùng mới", description = "Tạo một tài khoản người dùng mới trong hệ thống.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Người dùng đã được tạo thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Email đã tồn tại",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Có lỗi xảy ra trong quá trình đăng ký",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // API sửa người dùng
    @PutMapping("/{uid}")
    public ResponseEntity<User> updateUser(
            @PathVariable String uid,
            @RequestParam String email,
            @RequestParam String username) {
        User updatedUser = userService.updateUser(uid, email, username);
        return ResponseEntity.ok(updatedUser);
    }

    // API xóa người dùng
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteUser(@PathVariable String uid) {
        userService.deleteUser(uid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
