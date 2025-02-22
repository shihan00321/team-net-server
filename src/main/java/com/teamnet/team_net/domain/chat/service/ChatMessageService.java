package com.teamnet.team_net.domain.chat.service;

import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatService chatService;
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;
    public void sendMessage(String senderId, Long memberId, Long teamId, String message) {
        System.out.println("cms : " + TransactionSynchronizationManager.isActualTransactionActive());
        ChatResponse.ChatResponseDTO response = chatService.createMessage(memberId, teamId, message);
        for (SimpUser user : simpUserRegistry.getUsers()) {
            if (!senderId.equals(user.getPrincipal().getName())) {
                messagingTemplate.convertAndSendToUser(
                        user.getName(),
                        "/subscribe/topic/teams." + teamId,
                        response
                );
            }
        }
    }
}
