package com.thejoa703.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
// JPA관련 어노테이션
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/***
 * 사용자 엔티티
 */
@Entity   //JPA 엔티티 선언
@Table( name= "APPUSER" ,
	uniqueConstraints = @UniqueConstraint(
		name="UK_APPUSER_EMAIL_PROVIDER" ,	
		columnNames = {"EMAIL" , "PROVIDER"}
	)
)
@Getter  @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "appuser_seq")  //시퀀스 사용
	@SequenceGenerator(name = "appuser_seq", sequenceName = "APPUSER_SEQ" , allocationSize = 1)
	@Column(name="APP_USER_ID")
	private Long id; //PK
	
	@Column(length = 120  , nullable = false)
	private String email;  //이메일 (필수)

	@Column(length = 200  , nullable = true)
	private String password; // 소셜 로그인 null허용

	@Column(length = 50    , nullable = false)
	private String nickname; //닉네임
	
	@Column(name="MBTI_TYPE_ID")
	private Integer mbtitype;

	@Column(length = 255)
	private String ufile;
	
	@Column(length = 30)
	private String mobile;

	@Column(nullable = false , length = 50)
	private String provider="local";
	
	@Column(name="PROVIDER_ID" , length = 150)
	private String providerId="local";  // 소셜 provider에서 받은ID

	@Column(nullable = false , name="CREATED_AT")
	private LocalDateTime createdAt; // 생성일시
	
	@Column(nullable = false , name="UPDATED_AT")
	private LocalDateTime updatedAt; // 수정일시

	@Column
	private boolean deleted=false; // 삭제 여부
	 
	@Builder.Default
	@Column(nullable = false , length = 50)
	private String role="ROLE_USER"; // 기본 권한
	
	@PrePersist
	void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	void onUpdate() { 
		this.updatedAt = LocalDateTime.now();
	}
	
	// 테스트 생성자
	public AppUser(String email, String password, String nickname, String provider) {
		super();
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.provider = provider;
		this.role     = "ROLE_USER";
	}
	
	///// 한 사람이 여러글을 쓸수 있다. (OneToMany) , 
	///   mappedBy = "user" (누가 주인인지 지정) , User엔티티 posts 제거시 db에서도 제거
	@OneToMany( mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true )
	private List<Post> posts = new ArrayList<>();  // 유저가 작성한 게시글
	
	@OneToMany( mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true )
	private List<Comment> comments = new ArrayList<>();  // 유저가 작성한 댓글
	
	@OneToMany( mappedBy = "follower" , cascade = CascadeType.ALL , orphanRemoval = true )
	private List<Follow> followings = new ArrayList<>();  // 내가 팔로우한 사람들
	
	@OneToMany( mappedBy = "followee" , cascade = CascadeType.ALL , orphanRemoval = true )
	private List<Follow> followers = new ArrayList<>();  // 나를 팔로우하는 사람들 
	
	@OneToMany( mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true )
	private List<Retweet> retweets = new ArrayList<>();  // 나를 팔로우하는 사람들 
	
	@OneToMany( mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true )
	private List<PostLike> likes = new ArrayList<>();  // 유저가 누른 좋아요들
	
}









