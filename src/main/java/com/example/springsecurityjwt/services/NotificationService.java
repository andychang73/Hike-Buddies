package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.NotificationRepository;
import com.example.springsecurityjwt.models.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    public Notification save(Notification notification){
        return notificationRepository.save(notification);
    }

    public List<Notification> findAllByUserName(String userName){
        return notificationRepository.findAllByUserName(userName);
    }

    public Optional<Notification> findByNotificationIdAndUserName(int notificationId, String userName){
        return notificationRepository.findByNotificationIdAndUserName(notificationId, userName);
    }
}
