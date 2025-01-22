package com.teamnet.team_net.domain.post;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.DeletionStatus;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.repository.TeamRepository;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.exception.handler.PostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PostServiceTest {
    private static final String TEST_EMAIL = "xxx@xxx.com";
    private static final String TEST_NICKNAME = "hbb";
    private static final String TEST_TITLE = "테스트 제목";
    private static final String TEST_CONTENT = "테스트 내용";
    private static final String UPDATED_TITLE = "수정된 제목";
    private static final Long NON_EXISTENT_ID = 999L;

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private Member testMember;
    private Team testTeam;

    @BeforeEach
    void setUp() {
        testMember = createAndSaveMember();
        testTeam = createAndSaveTeam();
        createAndSaveTeamMember(testMember, testTeam);
    }

    @Test
    @DisplayName("게시글 단건 조회 성공 테스트")
    void findOne() {
        // given
        Post savedPost = createAndSavePost(testMember, testTeam);

        // when
        PostResponse.PostResponseDto foundPost = postService.findOne(savedPost.getId());

        // then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(foundPost.getContent()).isEqualTo(TEST_CONTENT);
        assertThat(foundPost.getId()).isEqualTo(savedPost.getId());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회시 예외 발생 테스트")
    void findOne_exception() {
        assertThrows(PostHandler.class,
                () -> postService.findOne(NON_EXISTENT_ID));
    }

    @Test
    @DisplayName("게시글 모두 조회")
    void findAllByTeamId() {
        // given
        List<Post> posts = IntStream.range(1, 6)
                .mapToObj(i -> Post.builder()
                        .title(TEST_TITLE + i)
                        .content(TEST_CONTENT + i)
                        .member(testMember)
                        .team(testTeam)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(posts);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        PostResponse.PostListResponseDto listResponseDto = postService.findAllByTeamId(testTeam.getId(), pageRequest);
        Page<PostResponse.PostResponseDto> findAll = listResponseDto.getPosts();

        // then
        assertThat(findAll).hasSize(5);
        assertThat(findAll.get().map(PostResponse.PostResponseDto::getTitle).equals(TEST_TITLE + "3"));
        assertThat(findAll.get().map(PostResponse.PostResponseDto::getContent).equals(TEST_TITLE + "3"));
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    void save() {
        // given
        PostServiceDTO.PostSaveServiceDTO postSaveDto = createPostSaveDto();

        // when
        PostResponse.PostResponseDto responseDto = postService.save(testMember.getId(), testTeam.getId(), postSaveDto);

        // then
        Post savedPost = postRepository.findById(responseDto.getId())
                .orElseThrow(() -> new AssertionError("저장된 게시글을 찾을 수 없습니다."));
        assertThat(savedPost.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(savedPost.getContent()).isEqualTo(TEST_CONTENT);
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void update() {
        // given
        Post savedPost = createAndSavePost(testMember, testTeam);
        PostServiceDTO.PostUpdateServiceDTO updateRequest = createPostUpdateDto();

        // when
        PostResponse.PostResponseDto update = postService.update(testMember.getId(), savedPost.getId(), updateRequest);
        PostResponse.PostResponseDto response = postService.findOne(update.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(response.getContent()).isEqualTo(TEST_CONTENT);
        assertThat(response.getId()).isEqualTo(update.getId());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 업데이트 시 예외")
    void update_exception() {
        // given
        PostServiceDTO.PostUpdateServiceDTO updateDto = createPostUpdateDto();

        // when & then
        assertThrows(PostHandler.class,
                () -> postService.update(testMember.getId(), NON_EXISTENT_ID, updateDto));
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void delete_success() {
        // given
        Post post = createAndSavePost(testMember, testTeam);

        // when
       postService.delete(testMember.getId(), post.getId());

        // then
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 시 예외")
    void delete_exception() {
        assertThrows(PostHandler.class,
                () -> postService.delete(testMember.getId(), NON_EXISTENT_ID));
    }

    private Member createAndSaveMember() {
        Member member = Member.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NICKNAME)
                .role(Role.USER)
                .status(DeletionStatus.NOT_DELETE)
                .build();
        return memberRepository.save(member);
    }

    private Team createAndSaveTeam() {
        Team team = Team.builder()
                .name("team")
                .status(TeamActiveStatus.ACTIVE)
                .build();
        return teamRepository.save(team);
    }

    private void createAndSaveTeamMember(Member member, Team team) {
        TeamMember teamMember = TeamMember.builder()
                .member(member)
                .team(team)
                .role(TeamRole.ADMIN)
                .build();
        teamMemberRepository.save(teamMember);
    }

    private Post createAndSavePost(Member member, Team team) {
        Post post = Post.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .member(member)
                .team(team)
                .comments(new ArrayList<>())
                .build();
        return postRepository.save(post);
    }

    private PostServiceDTO.PostSaveServiceDTO createPostSaveDto() {
        return PostServiceDTO.PostSaveServiceDTO.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .build();
    }

    private PostServiceDTO.PostUpdateServiceDTO createPostUpdateDto() {
        return PostServiceDTO.PostUpdateServiceDTO.builder()
                .title(UPDATED_TITLE)
                .content(TEST_CONTENT)
                .build();
    }
}