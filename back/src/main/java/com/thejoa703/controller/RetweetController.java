package com.thejoa703.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.thejoa703.dto.request.RetweetRequestDto;
import com.thejoa703.dto.response.RetweetResponseDto;
import com.thejoa703.service.AuthUserJwtService;
import com.thejoa703.service.RetweetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * ✅ RetweetController
 * - 리트윗 추가, 여부 확인, 취소, 카운트 조회, 내가 리트윗한 글 목록 조회
 */
@Tag(name = "Retweet", description = "리트윗 API")
@RestController
@RequestMapping("/api/retweets")
@RequiredArgsConstructor
public class RetweetController {

    private final RetweetService retweetService;
    private final AuthUserJwtService authUserJwtService;

    // 🔒 JWT 필요: 리트윗 추가
    @Operation(summary = "리트윗 추가 (JWT 인증 필요)")
    @PostMapping
    public ResponseEntity<RetweetResponseDto> addRetweet(
            Authentication authentication,
            @RequestBody RetweetRequestDto dto
    ) {
        Long userId = authUserJwtService.getCurrentUserId(authentication);
        return ResponseEntity.ok(retweetService.addRetweet(userId, dto)); // ✅ 변경: 응답에 retweetCount 포함
    }

    // 🔒 JWT 필요: 리트윗 여부 확인
    @Operation(summary = "리트윗 여부 확인 (JWT 인증 필요)")
    @GetMapping("/{postId}")
    public ResponseEntity<Boolean> hasRetweeted(
            Authentication authentication,
            @Parameter(description = "리트윗 여부를 확인할 게시글 ID")
            @PathVariable("postId") Long postId
    ) {
        Long userId = authUserJwtService.getCurrentUserId(authentication);
        return ResponseEntity.ok(retweetService.hasRetweeted(userId, postId));
    }

    // 🔒 JWT 필요: 리트윗 취소
    @Operation(summary = "리트윗 취소 (JWT 인증 필요)")
    @DeleteMapping("/{postId}")
    public ResponseEntity<RetweetResponseDto> removeRetweet( // ✅ 변경: Void → RetweetResponseDto
            Authentication authentication,
            @Parameter(description = "리트윗 취소할 게시글 ID")
            @PathVariable("postId") Long postId
    ) {
        Long userId = authUserJwtService.getCurrentUserId(authentication);
        return ResponseEntity.ok(retweetService.removeRetweet(userId, postId)); // ✅ 변경: 최신 카운트 포함 응답
    }

    // ✅ 추가: 특정 게시글의 리트윗 수 조회
    @Operation(summary = "특정 게시글의 리트윗 수 조회")
    @GetMapping("/count/{postId}")
    public ResponseEntity<Long> countRetweets(
            @Parameter(description = "리트윗 수를 확인할 게시글 ID")
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(retweetService.countRetweets(postId));
    }

    // ✅ 추가: 내가 리트윗한 글 목록 조회
    @Operation(summary = "내가 리트윗한 글 목록 조회 (JWT 인증 필요)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getMyRetweets(
            Authentication authentication,
            @Parameter(description = "리트윗한 글을 조회할 사용자 ID")
            @PathVariable("userId") Long userId
    ) {
        Long currentUserId = authUserJwtService.getCurrentUserId(authentication);
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.status(403).build(); // 권한 없음
        }
        return ResponseEntity.ok(retweetService.findMyRetweets(userId)); // ✅ 변경: 내가 리트윗한 글 목록 반환
    }
}
