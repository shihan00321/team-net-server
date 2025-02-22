package com.teamnet.team_net.domain.chat.controller;

import com.teamnet.team_net.domain.chat.service.ChatMessageService;
import com.teamnet.team_net.domain.chat.service.ChatService;
import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send.{teamId}")
    public void sendMessage(
            @DestinationVariable Long teamId,
            @Valid @RequestBody ChatRequest.CreateMessageDTO request,
            @LoginMember SessionMember sessionMember,
            @Header("simpUser") Principal principal) {
        String senderId = principal.getName();
        chatMessageService.sendMessage(senderId, sessionMember.getId(), teamId, request.getMessage());
    }
}





