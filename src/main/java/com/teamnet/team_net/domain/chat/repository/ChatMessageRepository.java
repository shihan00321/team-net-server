package com.teamnet.team_net.domain.chat.repository;

import com.teamnet.team_net.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByTeamId(Long teamId);
}
