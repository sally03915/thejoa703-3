package com.thejoa703.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponseDto {
    private Long postId; // ✅ 게시글 ID
    private Long count;  // ✅ 현재 좋아요 수
}
