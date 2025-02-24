package com.teamnet.team_net.domain.chat.service;

import com.teamnet.team_net.domain.chat.entity.ChatMessage;
import com.teamnet.team_net.domain.chat.repository.ChatMessageRepository;
import com.teamnet.team_net.domain.chat.service.dto.ChatResponse;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.teamnet.team_net.domain.chat.mapper.ChatMessageMapper.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final EntityChecker entityChecker;

    @Transactional
    public ChatResponse.ChatResponseDTO createMessage(Long memberId, Long teamId, String message) {
        TeamMember teamMember = entityChecker.findTeamMemberByMemberIdAndTeamId(memberId, teamId);
        ChatMessage chatMessage = chatMessageRepository.save(toChatMessage(message, teamMember.getMember(), teamMember.getTeam()));
        return toChatResponseDTO(chatMessage);
    }

    public List<ChatResponse.ChatResponseDTO> getChatHistory(Long memberId, Long teamId) {
        entityChecker.findTeamMemberByMemberIdAndTeamId(memberId, teamId);
        List<ChatMessage> chatMessages = chatMessageRepository.findByTeamId(teamId);
        return toChatListResponseDTO(chatMessages, memberId);
    }
}
