package com.teamnet.team_net.domain.notification.repository;

import com.teamnet.team_net.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n where n.member.id = :memberId")
    List<Notification> findNotifications(@Param("memberId") Long memberId);
}
