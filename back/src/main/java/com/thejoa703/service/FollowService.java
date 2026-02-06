// src/main/java/com/thejoa703/service/FollowService.java
package com.thejoa703.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thejoa703.dto.request.FollowRequestDto;
import com.thejoa703.dto.response.FollowResponseDto;
import com.thejoa703.dto.response.BlockResponseDto;
import com.thejoa703.entity.AppUser;
import com.thejoa703.entity.Follow; 
import com.thejoa703.repository.AppUserRepository; 
import com.thejoa703.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService { 
    private final FollowRepository followRepository; 
    private final AppUserRepository userRepository;
    //팔로우
    public FollowResponseDto follow(Long followerId, FollowRequestDto dto) {
        Long followeeId = dto.getFolloweeId();
        if (followerId.equals(followeeId)) {
            throw new IllegalStateException("자기 자신은 팔로우할 수 없습니다.");
        }

        //팔루워
        AppUser follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로워 없음"));
        //팔로위
        AppUser followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("팔로잉 대상 없음"));
        // jap 저장
        Follow saved = followRepository.save(new Follow(follower, followee));
        return FollowResponseDto.of(saved, followee, false);
    }
    // 언팔로우
    public Long unfollow(Long followerId, Long followeeId) {
        followRepository.findByFollower_IdAndFollowee_Id(followerId, followeeId)
            .ifPresent(followRepository::delete);
        return followeeId;
    } 
    //////////////////////////////////////////////////////
    // ✅ Followings 조회  
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowings(Long followerId) {
        return followRepository.findByFollower_Id(followerId).stream()
            .map(f -> FollowResponseDto.of(f, f.getFollowee(), false))  
            .collect(Collectors.toList());
    }
    // ✅ Followers 조회
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowers(Long followeeId) {
        return followRepository.findByFollowee_Id(followeeId).stream()
            .map(f -> FollowResponseDto.of(f, f.getFollower(), false))  
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countFollowings(Long followerId) {
        return getFollowings(followerId).size();
    }

    @Transactional(readOnly = true)
    public long countFollowers(Long followeeId) {
        return getFollowers(followeeId).size();
    }
}
