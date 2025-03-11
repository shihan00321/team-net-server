package com.teamnet.team_net.domain.chat.service;

import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topicPattern = "chat-messages-.*", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, ChatResponse.ChatResponseDTO> record) {
        String topic = record.topic();
        Long teamId = Long.parseLong(topic.substring("chat-messages-".length()));

        ChatResponse.ChatResponseDTO message = record.value();

        messagingTemplate.convertAndSend(
                "/subscribe/teams." + teamId,
                message
        );
    }

}

//        for (SimpUser user : simpUserRegistry.getUsers()) {
//            if (!senderId.equals(user.getName())) {
//                messagingTemplate.convertAndSendToUser(
//                        user.getName(),
//                        "/subscribe/teams." + teamId,
//                        message
//                );
//            }
//        }