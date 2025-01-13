package com.teamnet.team_net.domain.notification.repository;

import com.teamnet.team_net.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
