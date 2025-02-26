package com.teamnet.team_net.domain.chat.service;

import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatService chatService;
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(String senderId, Long memberId, Long teamId, String message) {
        ChatResponse.ChatResponseDTO response = chatService.createMessage(memberId, teamId, message);
        for (SimpUser user : simpUserRegistry.getUsers()) {
            if (!senderId.equals(user.getPrincipal().getName())) {
                messagingTemplate.convertAndSendToUser(
                        user.getName(),
                        "/subscribe/teams." + teamId,
                        response
                );
            }
        }
    }
}
