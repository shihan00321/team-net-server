package com.teamnet.team_net.domain.post.controller;

import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponse.PostResponseDto>> findAll() {
        return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse.PostResponseDto> findOne(@PathVariable("postId") Long postId) {
        return new ResponseEntity<>(postService.findOne(postId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> save(@LoginMember SessionMember sessionMember, @Valid @RequestBody PostRequest.PostSaveDto postSaveDto) {
        return ResponseEntity.ok(postService.save(sessionMember.getId(), postSaveDto));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Long> update(@LoginMember SessionMember sessionMember, @PathVariable("postId") Long postId, @Valid @RequestBody PostRequest.PostUpdateDto postUpdateDto) {
        return ResponseEntity.ok(postService.update(sessionMember.getId(), postId, postUpdateDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Long> delete(@LoginMember SessionMember sessionMember, @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.delete(sessionMember.getId(), postId));
    }
}
