package com.teamnet.team_net.domain.chat.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class ChatRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateMessageDTO {
        @NotBlank(message = "메시지는 비어있을 수 없습니다.")
        String message;
    }
}
