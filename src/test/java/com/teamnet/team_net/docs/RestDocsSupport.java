package com.teamnet.team_net.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.DeletionStatus;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.global.config.auth.LoginMemberArgumentResolver;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class})
public abstract class RestDocsSupport {

    protected MockMvc mvc;
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected MockHttpSession mockHttpSession;
    protected SessionMember sessionMember;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        sessionMember = createSessionMember();
        mockHttpSession = createMockHttpSession(sessionMember);

        LoginMemberArgumentResolver resolver = new LoginMemberArgumentResolver(mockHttpSession);
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        this.mvc = MockMvcBuilders.standaloneSetup(initController())
                .setCustomArgumentResolvers(resolver, pageableResolver)
                .apply(documentationConfiguration(provider))
                .build();
    }

    private SessionMember createSessionMember() {
        return new SessionMember(Member.builder()
                .id(1L)
                .name("hbb")
                .nickname("hbb")
                .email("xxx@xxx.com")
                .role(Role.USER)
                .status(DeletionStatus.NOT_DELETE)
                .build());
    }

    private MockHttpSession createMockHttpSession(SessionMember sessionMember) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);
        return session;
    }

    protected abstract Object initController();
}