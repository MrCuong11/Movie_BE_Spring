package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Phương thức sẵn có
    List<Notification> findByUser_UidAndIsReadFalse(String uid);

    // Thêm phương thức xóa các thông báo liên quan đến user_id
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.user.uid = :uid")
    void deleteByUserId(String uid);
}

