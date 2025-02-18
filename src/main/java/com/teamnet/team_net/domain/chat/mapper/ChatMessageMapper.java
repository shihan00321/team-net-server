package com.teamnet.team_net.domain.chat.mapper;

import com.teamnet.team_net.domain.chat.entity.ChatMessage;
import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.team.entity.Team;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ChatMessageMapper {
    public static ChatMessage toChatMessage(String message, Member sender, Team team) {
        return ChatMessage.builder()
                .sender(sender)
                .team(team)
                .message(message)
                .build();
    }

    public static ChatResponse.ChatResponseDTO toChatResponseDTO(ChatMessage chatMessage) {
        return ChatResponse.ChatResponseDTO.builder()
                .chatMessageId(chatMessage.getId())
                .message(chatMessage.getMessage())
                .senderNickName(chatMessage.getCreatedBy())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public static List<ChatResponse.ChatResponseDTO> toChatListResponseDTO(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(ChatMessageMapper::toChatResponseDTO)
                .collect(Collectors.toList());
    }
}
