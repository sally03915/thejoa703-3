package com.thejoa703.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thejoa703.dto.request.CommentRequestDto;
import com.thejoa703.dto.request.LikeRequestDto;
import com.thejoa703.dto.response.CommentResponseDto;
import com.thejoa703.dto.response.LikeResponseDto;
import com.thejoa703.entity.AppUser;
import com.thejoa703.entity.Post;
import com.thejoa703.entity.PostLike;
import com.thejoa703.repository.AppUserRepository;
import com.thejoa703.repository.PostLikeRepository;
import com.thejoa703.repository.PostRepository;

import lombok.RequiredArgsConstructor;

/**
 * 좋아요 서비스
 * - 좋아요 추가, 취소, 카운트, 여부 확인
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final AppUserRepository  userRepository;
    private final PostRepository     postRepository;
 
    //////  좋아요 생성 
    public LikeResponseDto addLike( Long userId , LikeRequestDto dto    ) {
        // 사용자 조회
    		AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));  

    		Post    post = postRepository.findById(  dto.getPostId()   )
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));  
    		
    		// 중복 좋아요 방지
    		if( postLikeRepository.countByUser_IdAndPost_Id(userId, dto.getPostId())  > 0 ) {  // 기존에 있다 1개
    	        long count = postLikeRepository.countByPost_Id(post.getId());   //현재좋아요수 반환
    	        return LikeResponseDto.builder()
    	                .postId(  post.getId())
    	                .count(count)
    	                .build();
    	    		
    		}
    		// 좋아요 저장
    		postLikeRepository.save(  new PostLike(user,post) );
    		// 최신 좋아요 수 반환
        long count = postLikeRepository.countByPost_Id(post.getId());  
        return LikeResponseDto.builder()
                .postId(  post.getId())
                .count(count)
                .build();
    		
    }

    // 특정게시글의 좋아요 수
    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        return postLikeRepository.countByPost_Id(postId);  
    }
    // 특정유저가 특정게시글의 좋아요 여부
    @Transactional(readOnly = true)
    public boolean hasLiked(Long userId, Long postId) {
        return postLikeRepository.countByUser_IdAndPost_Id(userId, postId) > 0;  
    }
 
    // 좋아요 취소
    public LikeResponseDto removeLike(Long userId, Long postId) {
        postLikeRepository.deleteByUserAndPost(userId, postId);  
 
        long updatedCount = postLikeRepository.countByPost_Id(postId);  
        return LikeResponseDto.builder()
                .postId(postId)
                .count(updatedCount)
                .build();
    }
}
