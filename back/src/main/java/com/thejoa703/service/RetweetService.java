package com.thejoa703.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.thejoa703.dto.request.RetweetRequestDto;
import com.thejoa703.dto.response.RetweetResponseDto;
import com.thejoa703.entity.AppUser;
import com.thejoa703.entity.Post;
import com.thejoa703.entity.Retweet;
import com.thejoa703.repository.AppUserRepository;
import com.thejoa703.repository.PostRepository;
import com.thejoa703.repository.RetweetRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class RetweetService {

    private final RetweetRepository retweetRepository;
    private final AppUserRepository userRepository;
    private final PostRepository postRepository;
    // 리트윗추가
    public RetweetResponseDto addRetweet(Long userId, RetweetRequestDto dto) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Post post = postRepository.findById(dto.getOriginalPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (retweetRepository.countByUserAndOriginalPost(userId, dto.getOriginalPostId()) > 0) {
            throw new IllegalStateException("이미 리트윗한 게시글입니다.");
        }

        Retweet saved = retweetRepository.save(new Retweet(user, post));
        long count = retweetRepository.countByOriginalPostId(post.getId());  

        return RetweetResponseDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .originalPostId(post.getId())
                .createdAt(saved.getCreatedAt())
                .retweetCount(count) 
                .build();
    }

    // 특정유저가 특정게시글의 리트윗했는지 여부
    @Transactional(readOnly = true)
    public boolean hasRetweeted(Long userId, Long postId) {
        return retweetRepository.countByUserAndOriginalPost(userId, postId) > 0;
    }

    // 게시글의 리트윗수
    @Transactional(readOnly = true)
    public long countRetweets(Long postId) {
        return retweetRepository.countByOriginalPostId(postId);
    }

    // 리트윗 취소
    public RetweetResponseDto removeRetweet(Long userId, Long postId) {
        Retweet retweet = retweetRepository.findByUserAndOriginalPost(userId, postId)
                .orElseThrow(() -> new IllegalStateException("리트윗 없음"));

        if (!retweet.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한 없음");
        }

        retweetRepository.delete(retweet);
        long count = retweetRepository.countByOriginalPostId(postId);  

        return RetweetResponseDto.builder()
                .id(retweet.getId())
                .userId(userId)
                .originalPostId(postId)
                .createdAt(retweet.getCreatedAt())
                .retweetCount(count)  
                .build();
    }
 
	@Transactional(readOnly = true)
	public List<Long> findMyRetweets(Long userId) {
	    return retweetRepository.findOriginalPostIdsByUserId(userId);  
	}
}
