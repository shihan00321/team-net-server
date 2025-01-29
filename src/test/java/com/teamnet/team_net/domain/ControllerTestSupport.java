package com.teamnet.team_net.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.comment.controller.CommentController;
import com.teamnet.team_net.domain.comment.service.CommentService;
import com.teamnet.team_net.domain.post.controller.PostController;
import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.domain.team.controller.TeamController;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.global.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = {TeamController.class, PostController.class, CommentController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected PostService postService;

    @MockBean
    protected TeamService teamService;
}
