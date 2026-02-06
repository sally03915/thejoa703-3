package com.thejoa703.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.thejoa703.entity.Post;
import com.thejoa703.entity.Retweet;
 
public interface RetweetRepository extends JpaRepository<Retweet, Long> {

    // 특정 유저의 특정 게시글 리트윗 조회 (JPQL로 직접 작성 → Oracle 11g 호환)
    @Query("SELECT r FROM Retweet r WHERE r.user.id = :userId AND r.originalPost.id = :postId")
    Optional<Retweet> findByUserAndOriginalPost(@Param("userId") Long userId, @Param("postId") Long postId);

    // 중복 리트윗 방지 (count 기반 체크)
    @Query("SELECT COUNT(r) FROM Retweet r WHERE r.user.id = :userId AND r.originalPost.id = :postId")
    long countByUserAndOriginalPost(@Param("userId") Long userId, @Param("postId") Long postId);

    // 리트윗 취소 (삭제 쿼리 실행 시 트랜잭션 필요)
    @Modifying
    @Transactional
    @Query("DELETE FROM Retweet r WHERE r.user.id = :userId AND r.originalPost.id = :postId")
    void deleteByUserAndOriginalPost(@Param("userId") Long userId, @Param("postId") Long postId);

    // 특정 게시글의 리트윗 수 집계
    long countByOriginalPostId(Long postId);
    
    // ✅ 변경: 특정 유저가 리트윗한 글 목록 조회
    @Query("SELECT r.originalPost.id FROM Retweet r WHERE r.user.id = :userId")
    List<Long> findOriginalPostIdsByUserId(@Param("userId") Long userId);
    
    ///////////////////////////////////////////////////// 내가 리트윗
    @Query(
      value = "SELECT * FROM ( " +
              "SELECT p.*, ROWNUM AS rnum " +
              "FROM ( " +
              "   SELECT po.* " +
              "   FROM POSTS po " +
              "   WHERE po.ID IN ( " +
              "       SELECT DISTINCT r.ORIGINAL_POST_ID " +
              "       FROM RETWEETS r " +
              "       WHERE r.APP_USER_ID = :userId " +
              "   ) AND po.deleted = 0 " +
              "   ORDER BY po.created_at DESC " +
              ") p " +
              ") " +
              "WHERE rnum BETWEEN :start AND :end",
      nativeQuery = true
    )
    List<Post> findRetweetedPostsWithPaging(@Param("userId") Long userId,
                                            @Param("start") int start,
                                            @Param("end") int end);

}
//Spring Data JPA 메서드 이름 규칙 