package com.teamnet.team_net.domain.post.controller;

import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.PostService;
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
    public ResponseEntity<Long> save(@Valid @RequestBody PostRequest.PostSaveDto postSaveDto) {
        return ResponseEntity.ok(postService.save(postSaveDto));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Long> update(@PathVariable("postId") Long postId, @Valid @RequestBody PostRequest.PostUpdateDto postUpdateDto) {
        return ResponseEntity.ok(postService.update(postId, postUpdateDto));
    }
}
