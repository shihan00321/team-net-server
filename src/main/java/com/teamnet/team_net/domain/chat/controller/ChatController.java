package com.teamnet.team_net.domain.chat.controller;

import com.teamnet.team_net.domain.chat.service.ChatService;
import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.send.{teamId}") // 해당 주소(WebSocket Prefix /publish + /chat.send.{teamId} 로 발행된 메시지를
    @SendTo("/topic/teams.{teamId}") // 해당 주소(WebSocket Prefix /subscribe + /topic/teams.{teamId})를 구독한 사용자에게 전달
    public ApiResponse<ChatResponse.ChatResponseDTO> sendMessage(
            @LoginMember SessionMember sessionMember,
            @DestinationVariable Long teamId,
            @Valid @RequestBody ChatRequest.CreateMessageDTO request) {
        return ApiResponse.onSuccess(chatService.createMessage(sessionMember.getId(), teamId, request.getMessage()));
    }

}
