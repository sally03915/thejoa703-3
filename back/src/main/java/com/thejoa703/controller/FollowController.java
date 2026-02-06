// src/main/java/com/thejoa703/controller/FollowController.java
package com.thejoa703.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thejoa703.dto.request.BlockRequestDto;
import com.thejoa703.dto.request.FollowRequestDto;
import com.thejoa703.dto.response.BlockResponseDto;
import com.thejoa703.dto.response.FollowResponseDto;
import com.thejoa703.service.AuthUserJwtService;
import com.thejoa703.service.FollowService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final AuthUserJwtService authUserJwtService;

    @PostMapping
    public ResponseEntity<?> follow(Authentication authentication,
                                    @Valid @RequestBody FollowRequestDto dto) {
        try {
            Long followerId = authUserJwtService.getCurrentUserId(authentication);
            FollowResponseDto body = followService.follow(followerId, dto);
            // Idempotent: always 200 OK with current state
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/me/followings")
    public ResponseEntity<List<FollowResponseDto>> getMyFollowings(Authentication authentication) {
        Long followerId = authUserJwtService.getCurrentUserId(authentication);
        return ResponseEntity.ok(followService.getFollowings(followerId));
    }

    @GetMapping("/me/followers")
    public ResponseEntity<List<FollowResponseDto>> getMyFollowers(Authentication authentication) {
        Long followeeId = authUserJwtService.getCurrentUserId(authentication);
        return ResponseEntity.ok(followService.getFollowers(followeeId));
    }

    @GetMapping("/me/followings/count")
    public ResponseEntity<Long> countMyFollowings(Authentication authentication) {
        Long followerId = authUserJwtService.getCurrentUserId(authentication);
        return ResponseEntity.ok(followService.countFollowings(followerId));
    }

    @GetMapping("/me/followers/count")
    public ResponseEntity<Long> countMyFollowers(Authentication authentication) {
        Long followeeId = authUserJwtService.getCurrentUserId(authentication);
        return ResponseEntity.ok(followService.countFollowers(followeeId));
    }

    @DeleteMapping
    public ResponseEntity<?> unfollow(Authentication authentication,
                                      @Valid @RequestBody FollowRequestDto dto) {
        Long followerId = authUserJwtService.getCurrentUserId(authentication);
        Long followeeId = followService.unfollow(followerId, dto.getFolloweeId());
        return ResponseEntity.ok().body(followeeId);
    }

 
}
