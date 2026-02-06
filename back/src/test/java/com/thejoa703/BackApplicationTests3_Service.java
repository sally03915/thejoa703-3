package com.thejoa703;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.thejoa703.dto.request.CommentRequestDto;
import com.thejoa703.dto.request.FollowRequestDto;
import com.thejoa703.dto.request.LikeRequestDto;
import com.thejoa703.dto.request.LoginRequest;
import com.thejoa703.dto.request.PostRequestDto;
import com.thejoa703.dto.request.RetweetRequestDto;
import com.thejoa703.dto.request.UserRequestDto;
import com.thejoa703.dto.response.CommentResponseDto;
import com.thejoa703.dto.response.FollowResponseDto;
import com.thejoa703.dto.response.LikeResponseDto;
import com.thejoa703.dto.response.PostResponseDto;
import com.thejoa703.dto.response.RetweetResponseDto;
import com.thejoa703.dto.response.UserResponseDto;
import com.thejoa703.service.AppUserService;
import com.thejoa703.service.CommentService;
import com.thejoa703.service.FollowService;
import com.thejoa703.service.PostLikeService;
import com.thejoa703.service.PostService;
import com.thejoa703.service.RetweetService;
 
 
@SpringBootTest
@Transactional  // org.springframework.transaction.annotation.Transactional
class BackApplicationTests3_Service {
	//서비스
	@Autowired  private AppUserService    appUserSerivce;
	@Autowired  private PostService       postService;
	@Autowired  private CommentService    commentService;
	@Autowired  private PostLikeService   postLikeService;
	@Autowired  private FollowService     followService;
	@Autowired  private RetweetService    retweetService;
	
	//테스트 공통 데이터 
	private UserResponseDto user1Dto;
	private UserResponseDto user2Dto;
	private PostResponseDto post;
	/** 공통 준비 : 사용자 2명 + 게시글 1글 */ 
	@BeforeEach
	void setup() {
		//사용자 생성 																email		password		nickname  provider
		UserRequestDto req1 = new UserRequestDto("user1" + UUID.randomUUID() + "@test.com" , "pass123" , "user1" , "local");
		UserRequestDto req2 = new UserRequestDto("user2" + UUID.randomUUID() + "@test.com" , "pass123" , "user2" , "local");
		
		user1Dto = appUserSerivce.signup(req1, null);
		user2Dto = appUserSerivce.signup(req2, null);   //UserRequestDto request, MultipartFile profileImage
		//게시글 생성 
		
		PostRequestDto postReq = new PostRequestDto("테스트 게시글" , "#tag1,#tag2");
		post = postService.createPost(user1Dto.getId(), postReq, null);
		
	}
    // ---------------------------------------------------------------------
    // AppUserService
    // ---------------------------------------------------------------------
	@Test
	@DisplayName("■ AppUserService-CRUD ")
	void testAppUserService() { 
		// 로그인성공   org.assertj.core.api.Assertions.assertThat
		LoginRequest loginReq = new LoginRequest(  user1Dto.getEmail() , "pass123" , "local" );
		UserResponseDto loginUser = appUserSerivce.login(loginReq);
		assertThat(    loginUser.getEmail()     ).isEqualTo(       user1Dto.getEmail()    );
		
		// 로그인실패 (비밀번호 불일치)
		LoginRequest wrongReq = new LoginRequest(  user1Dto.getEmail() , "wrong" , "local" );
		assertThrows(     IllegalArgumentException.class , () -> appUserSerivce.login(wrongReq)    );
		
		// 닉네임변경
		UserResponseDto  updated = appUserSerivce.updateNickname( user1Dto.getId() , "newNick");
		assertThat(    updated.getNickname()     ).isEqualTo(       "newNick"   ); 
		
		// 프로필이미지 변경
		MultipartFile file = new MockMultipartFile("file", "test.png" , "image/png" , "dummy".getBytes());
		UserResponseDto  updatedImg = appUserSerivce.updateProfileImage( user1Dto.getId(), file);
		assertThat(  updatedImg.getUfile() ).contains("uploads/");
		
		// 삭제 후 조회 불가
		appUserSerivce.deleteById(   user1Dto.getId()   );
		assertThrows(     IllegalArgumentException.class , () -> appUserSerivce.findById( user1Dto.getId() ) );
	}  
	
	
    // ---------------------------------------------------------------------
    // PostService
    // ---------------------------------------------------------------------
	@Test
	@DisplayName("■ PostService-CRUD ")
	void testPostService() { 
		//게시글 단건조회
		PostResponseDto found = postService.getPost(post.getId());
		assertThat(    found.getContent()     ).isEqualTo(       "테스트 게시글"   ); 
		
		//게시글 수정
		PostRequestDto updateReq = new PostRequestDto("수정된 게시글" , "#newTag");
		PostResponseDto updated  = postService.updatePost(user1Dto.getId(), post.getId(), updateReq, null);
		assertThat(    updated.getContent()     ).isEqualTo(       "수정된 게시글"   ); 
		
		//해시태그검색  
		 List<PostResponseDto>  byTag =   postService.getPostsByHashtag("#newTag");
		 assertThat(    byTag     ).isNotEmpty();
		
		 //deletePost
		 postService.deletePost( user1Dto.getId(), post.getId());
		 assertThrows(     IllegalArgumentException.class , () -> postService.getPost(post.getId())   );
		 
	}
	
	
    // ---------------------------------------------------------------------
    // CommentService
    // ---------------------------------------------------------------------
	@Test
	@DisplayName("■ CommentService-CRUD ")
	void testCommentService() { 
		// 댓글작성
		CommentRequestDto   commentReq = new CommentRequestDto( post.getId() , "테스트 댓글" );
		CommentResponseDto  comment    = commentService.createComment(user2Dto.getId() , commentReq  );
		assertThat(    comment.getContent()     ).isEqualTo(       "테스트 댓글"   ); 
		
		// 댓글조회
		List<CommentResponseDto>  comments =  commentService.getCommentsByPost(post.getId());
		assertThat( comments ).hasSize(1);
		
		// 댓글수정
		CommentRequestDto   updateReq = new CommentRequestDto( post.getId() , "수정된 댓글" );
		CommentResponseDto  updated   = commentService.updateComment(user2Dto.getId() , comment.getId() , updateReq );
		assertThat(    updated.getContent()     ).isEqualTo(       "수정된 댓글"   ); 
		
		// 댓글삭제
		commentService.deleteComment(user2Dto.getId() , comment.getId());
		assertThat(  commentService.countComments(post.getId())      ).isEqualTo(0);
	}


	// ---------------------------------------------------------------------
    // ■ PostLikeService 테스트
    // ---------------------------------------------------------------------
    @Test
    @DisplayName("■ PostLikeService - 좋아요 추가/중복 방지/취소")
    void testPostLikeService() { 
    		//좋아요 추가
        LikeRequestDto likeReq = new LikeRequestDto(post.getId());
        LikeResponseDto like = postLikeService.addLike(user2Dto.getId(), likeReq);
        assertThat(like.getCount()).isEqualTo(1);  
 
        //중복 좋아요
        LikeResponseDto duplicate = postLikeService.addLike(user2Dto.getId(), likeReq);
        assertThat(duplicate.getCount()).isEqualTo(1);
        
        //좋아요 취소
        LikeResponseDto removed = postLikeService.removeLike(user2Dto.getId(), post.getId());
        assertThat( removed.getCount() ).isEqualTo(0);
    }
    	
    // ---------------------------------------------------------------------
    // FollowService 테스트
    // ---------------------------------------------------------------------
    @Test
    @DisplayName("■ FollowService - 팔로우/언팔로우/차단/차단해제")
    void testFollowService() {
    		//팔로우
        FollowRequestDto followReq = new FollowRequestDto(user2Dto.getId());
        FollowResponseDto follow = followService.follow(user1Dto.getId(), followReq);  //팔로워, 팔로위
        assertThat(follow.getFolloweeId()).isEqualTo(user2Dto.getId());
        	//자기자신 팔로우 → 예외
        FollowRequestDto selfFollow = new FollowRequestDto(user1Dto.getId());
        assertThrows(IllegalStateException.class, () -> followService.follow(user1Dto.getId(), selfFollow));
        //언팔로우
        Long unfollowedId = followService.unfollow(user1Dto.getId(), user2Dto.getId());
        assertThat(unfollowedId).isEqualTo(user2Dto.getId());
  
    }

	// ---------------------------------------------------------------------
	// RetweetService 테스트
	// ---------------------------------------------------------------------
	@Test
	@DisplayName("■ RetweetService - 리트윗 추가/중복/조회/취소/목록")
	void testRetweetService() { 
		//1. 작성게시글 준비
	    RetweetRequestDto retweetReq = new RetweetRequestDto(post.getId()); 
	    //													 어떤유저가			원본글
	    RetweetResponseDto retweet = retweetService.addRetweet(user1Dto.getId(), retweetReq);
	    assertThat(retweet.getOriginalPostId()).isEqualTo(post.getId()); //post
	    assertThat(retweet.getUserId()).isEqualTo(user1Dto.getId());   //user1Dto
	    assertThat(retweet.getRetweetCount()).isEqualTo(1);  // 리트윗수가 1개야
	    // 중복리트윗 → 예외
	    assertThrows(IllegalStateException.class,
	        () -> retweetService.addRetweet(user1Dto.getId(), retweetReq));
	    // 리트윗여부
	    boolean hasRetweeted = retweetService.hasRetweeted(user1Dto.getId(), post.getId());
	    assertThat(hasRetweeted).isTrue();
	    // 리트윗수 확인
	    long count = retweetService.countRetweets(post.getId());
	    assertThat(count).isEqualTo(1);
	    // 리트윗 취소
	    RetweetResponseDto removed = retweetService.removeRetweet(user1Dto.getId(), post.getId());
	    assertThat(removed.getRetweetCount()).isEqualTo(0);
 
	}
	
}

/*
          사용자      관리자
CREATE    ◎회원가입    ◎회원가입
READ      로그인, 이메일중복, 닉네임중복 
UPDATE    ◎닉네임수정, ◎이미지수정
DELETE    ◎회원탈퇴
*/


