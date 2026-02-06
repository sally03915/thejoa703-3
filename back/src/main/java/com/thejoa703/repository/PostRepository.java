package com.thejoa703.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thejoa703.entity.Post;
 

@Repository  //★
public interface PostRepository extends JpaRepository<Post, Long> { //Entity , PK ★
	// 해쉬태그 이름으로 게시글 검색 (글 쓰기후 검색확인)
	List<Post> findByHashtags_NameAndDeletedFalse(String name);

	// 조회 :  삭제되지 않은 게시글 
	List<Post> findByDeletedFalse();
	
	// 전체게시글 조회 - Oracle 네이티브 페이징  
	//org.springframework.data.jpa.repository.Query;
	//org.springframework.data.repository.query.Param  ##
    @Query(
    	      value = "SELECT * FROM ( " +
    	              "SELECT p.*, ROWNUM AS rnum " +
    	              "FROM (SELECT * FROM POSTS WHERE DELETED = 0 ORDER BY CREATED_AT DESC) p " + 
    	              ") " +
    	              "WHERE rnum BETWEEN :start AND :end",
    	      nativeQuery = true
    	)  
    List<Post> findPostsWithPaging(@Param("start") int start, @Param("end") int end);  //  오타  ##
 
    // 특정유저가 좋아요한 게시물
    @Query(
    	      value = "SELECT * FROM ( " +
    	              "SELECT p.*, ROWNUM AS rnum " +
    	              "FROM ( " +
    	              "   SELECT po.* " +
    	              "   FROM POSTS po " +
    	              "   WHERE po.ID IN ( " +
    	              "       SELECT DISTINCT pl.POST_ID " +
    	              "       FROM POST_LIKES pl " +
    	              "       WHERE pl.APP_USER_ID = :userId " +
    	              "   ) AND po.DELETED = 0 " +  
    	              "   ORDER BY po.CREATED_AT DESC " +  
    	              ") p " +
    	              ") " +
    	              "WHERE rnum BETWEEN :start AND :end",
    	      nativeQuery = true
    	 ) 
    List<Post> findLikedPostsWithPaging(@Param("userId") Long userId , @Param("start") int start, @Param("end") int end);
	    	     
    // 내가 쓴글 + 내가 리트윗한 글 (합쳐서 조회)
    @Query(
    	      value = "SELECT * FROM ( " +
    	              "SELECT p.*, ROWNUM AS rnum " +
    	              "FROM ( " +
    	              "   SELECT po.ID, po.CONTENT, po.CREATED_AT, po.DELETED, po.UPDATED_AT, po.APP_USER_ID " +  
    	              "   FROM POSTS po " +
    	              "   WHERE po.APP_USER_ID = :userId AND po.DELETED = 0 " +
    	              "   UNION ALL " +
    	              "   SELECT po.ID, po.CONTENT, po.CREATED_AT, po.DELETED, po.UPDATED_AT, po.APP_USER_ID " + 
    	              "   FROM POSTS po " +
    	              "   WHERE po.ID IN ( " +
    	              "       SELECT DISTINCT r.ORIGINAL_POST_ID " +
    	              "       FROM RETWEETS r " +
    	              "       WHERE r.APP_USER_ID = :userId " +
    	              "   ) AND po.DELETED = 0 " +
    	              "   ORDER BY CREATED_AT DESC " +  
    	              ") p " +
    	              ") " +
    	              "WHERE rnum BETWEEN :start AND :end",
    	      nativeQuery = true
    	    )  
    List<Post> findMyPostsAndRetweetsWithPaging(@Param("userId") Long userId , @Param("start") int start, @Param("end") int end);

    /////////////////////////////////
    //List<Post>  findPostsWithPaging( @Param("start") int start, @Param("end") int end );
}
 
/*
CREATE : save     -   INSERT INTO  테이블명 (컬럼1,컬럼2,,) values (?,?,,)
READ   : findAll  -   SELECT  * from 테이블명  
         findById -   SELECT  * from 테이블명   where id=? 
UPDATE : save     -   update  테이블명   set 컬럼1=? ,컬럼2=?  where   id=? 
DELETE : deleteById - delete from 테이블명   where id=?
*/