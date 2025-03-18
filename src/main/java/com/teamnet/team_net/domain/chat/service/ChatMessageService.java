package com.teamnet.team_net.domain.chat.service;

import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatService chatService;
    private final KafkaTemplate<String, ChatResponse.ChatResponseDTO> kafkaTemplate;

    public void sendMessage(String senderId, Long memberId, Long teamId, String message) {
        ChatResponse.ChatResponseDTO response = chatService.createMessage(memberId, teamId, message);

        // Kafka에 메시지 전송 (senderId를 key로 사용)
        String topic = "chat-messages-" + teamId;
        kafkaTemplate.send(topic, senderId, response);
    }
}
