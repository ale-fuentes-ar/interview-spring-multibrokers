package com.ale.notification_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ale.notification_service.model.Notification;
import com.ale.notification_service.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired private NotificationService service;

    @GetMapping
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }

    @PostMapping
    public Notification create(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        return service.createNotification(message);
    }
    
}
