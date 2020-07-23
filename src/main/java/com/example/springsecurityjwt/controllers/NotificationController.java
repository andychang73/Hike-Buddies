package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.models.Notification;
import com.example.springsecurityjwt.services.NotificationService;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    JwtUtil jwtUtil;

    @GetMapping
    public Status findAllByUserName(@RequestHeader("Authorization") String jwt){
        String userName = jwtUtil.extractUsername(jwt);
        List<Notification> notifications = notificationService.findAllByUserName(userName);
        if(notifications.isEmpty()){
            return new Status (400, "No notifications!");
        }
        return new Status (200, notifications);
    }

    @PostMapping("/update")
    public Status updateStatus(@RequestHeader("Authorization") String jwt,
                               @RequestBody Notification notification){
        if(notification.getStatus() != 1 && notification.getStatus() != 2){
            return new Status (400, "Invalid status parameter!");
        }
        String userName = jwtUtil.extractUsername(jwt);
        Optional<Notification> not = notificationService.findByNotificationIdAndUserName(notification.getNotificationId(),
                                                                                          userName);
        if(not.isEmpty()){
            return new Status (401, "No such notification!");
        }
        not.get().setStatus(notification.getStatus());
        return new Status (200, not.get());
    }
}
