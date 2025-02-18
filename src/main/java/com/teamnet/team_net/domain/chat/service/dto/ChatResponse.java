package com.teamnet.team_net.domain.chat.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ChatResponseDTO {
        Long chatMessageId;
        String message;
        String senderNickName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt;
    }
}
