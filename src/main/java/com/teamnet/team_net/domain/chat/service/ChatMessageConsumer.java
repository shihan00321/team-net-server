package com.teamnet.team_net.domain.chat.service;

import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(topicPattern = "chat-messages-.*", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, ChatResponse.ChatResponseDTO> record) {
        String topic = record.topic();
        Long teamId = Long.parseLong(topic.substring("chat-messages-".length()));

        ChatResponse.ChatResponseDTO message = record.value();
        String senderId = record.key();
        String redisKey = "team:sessions:" + teamId;

        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        Set<String> teamUsers = setOperations.members(redisKey);

        if (teamUsers != null && !teamUsers.isEmpty()) {
            for (String userName : teamUsers) {
                if (!userName.equals(senderId)) {
                    messagingTemplate.convertAndSendToUser(
                            userName,
                            "/subscribe/teams." + teamId,
                            message
                    );
                }
            }
        }
    }
}
