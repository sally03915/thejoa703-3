// src/main/java/com/thejoa703/dto/response/FollowResponseDto.java
package com.thejoa703.dto.response;

import java.time.LocalDateTime;
import com.thejoa703.entity.AppUser;
import com.thejoa703.entity.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class FollowResponseDto {
    private Long id;
    private Long followerId;
    private Long followeeId;
    private LocalDateTime createdAt;

    private String nickname;
    private String email;
    private String ufile;

    private boolean blocked;  

    public static FollowResponseDto of(Follow follow, AppUser targetUser, boolean blocked) {
        FollowResponseDto dto = new FollowResponseDto();
        dto.id = follow.getId();
        dto.followerId = follow.getFollower().getId();
        dto.followeeId = follow.getFollowee().getId();
        dto.createdAt = follow.getCreatedAt();

        dto.nickname = targetUser.getNickname();
        dto.email = targetUser.getEmail();
        dto.ufile = targetUser.getUfile();

        dto.blocked = blocked;
        return dto;
    }
}
