// src/main/java/com/thejoa703/dto/response/BlockResponseDto.java
package com.thejoa703.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class BlockResponseDto {
    private Long blockerId;
    private Long blockedUserId;
    private boolean blocked;
}
