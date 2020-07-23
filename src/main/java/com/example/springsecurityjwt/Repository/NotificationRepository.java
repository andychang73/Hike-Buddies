package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByUserName(String userName);

    Optional<Notification> findByNotificationIdAndUserName(int notificationId, String userName);
}
