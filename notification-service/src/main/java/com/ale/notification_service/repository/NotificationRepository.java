package com.ale.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ale.notification_service.model.*;

public interface NotificationRepository extends JpaRepository<Notification, Long> {    
}
