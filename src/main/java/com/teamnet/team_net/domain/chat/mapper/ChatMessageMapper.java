package com.teamnet.team_net.domain.chat.mapper;

import com.teamnet.team_net.domain.chat.entity.ChatMessage;
import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.team.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "id", ignore = true)
    ChatMessage toChatMessage(String message, Member sender, Team team);

    @Mapping(target = "isMine", expression = "java(isMine(chatMessage, memberId))")
    @Mapping(target = "chatMessageId", source = "chatMessage.id")
    @Mapping(target = "senderNickName", source = "chatMessage.createdBy")
    @Mapping(target = "senderId", source = "chatMessage.sender.id")
    ChatResponse.ChatResponseDTO toChatResponseDTO(ChatMessage chatMessage, Long memberId);

    default List<ChatResponse.ChatResponseDTO> toChatListResponseDTO(List<ChatMessage> chatMessages, Long memberId) {
        return chatMessages.stream()
                .map(chatMessage -> toChatResponseDTO(chatMessage, memberId))
                .collect(Collectors.toList());
    }

    default boolean isMine(ChatMessage chatMessage, Long memberId) {
        return memberId != null && memberId.equals(chatMessage.getSender().getId());
    }
}
