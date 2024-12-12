package com.movie.Movie_BE.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserService {

    // Xóa người dùng trên Firebase
    @Async
    public void deleteUserFromFirebase(String uid) {
        if (uid == null || uid.isEmpty()) {
            System.err.println("UID is null or empty, skipping Firebase delete.");
            return;
        }

        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            System.out.println("User deleted from Firebase: " + uid);
        } catch (Exception e) {
            System.err.println("Error deleting user from Firebase: " + e.getMessage());
        }
    }

    // Cập nhật người dùng trên Firebase
    @Async
    public void updateUserInFirebase(String uid, String email, String userName) {
        try {
            FirebaseAuth.getInstance().updateUser(
                    new UserRecord.UpdateRequest(uid)
                            .setEmail(email)
                            .setDisplayName(userName)
            );
            System.out.println("User updated on Firebase: " + uid);
        } catch (Exception e) {
            System.err.println("Error updating user on Firebase: " + e.getMessage());
        }
    }
}
