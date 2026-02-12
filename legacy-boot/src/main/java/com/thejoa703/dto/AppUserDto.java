package com.thejoa703.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto {
	private Integer  appUserId;   //pk
	private String email;       //이메일
	private String password;    
	private Integer  mbtiTypeId;  
	private String createdAt;  
	private String ufile;       //프로필이미지
	private String mobile;      //휴대폰
	private String nickname;
	private String provider;    //google, kakao, naver, local
	private String providerId;  // 각provider의 고유id
	
	public AppUserDto(String email, String provider) {
		super();
		this.email = email;
		this.provider = provider;
	} 
}
 