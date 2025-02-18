package com.teamnet.team_net.domain.chat.controller;

import com.teamnet.team_net.domain.chat.service.ChatService;
import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/chats")
@RestController
public class ChatApiController {
    private final ChatService chatService;

    @GetMapping
    public ApiResponse<List<ChatResponse.ChatResponseDTO>> getChatHistory(
            @LoginMember SessionMember sessionMember,
            @PathVariable Long teamId
    ) {
        return ApiResponse.onSuccess(chatService.getChatHistory(sessionMember.getId(), teamId));
    }
}
