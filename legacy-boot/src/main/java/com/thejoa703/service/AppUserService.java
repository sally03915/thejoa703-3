package com.thejoa703.service;

import org.springframework.web.multipart.MultipartFile;

import com.thejoa703.dto.AppUserAuthDto;
import com.thejoa703.dto.AppUserDto;

public interface AppUserService {
	/** 회원가입 (유저정보삽입 + 권한 + 비밀번호암호화) **/
	public int insert( MultipartFile file, AppUserDto dto );
	
	/** 회원정보 수정- local **/
	public int update( MultipartFile file, AppUserDto dto );
	
	/** 회원탈퇴 - local true / 소셜계정 false **/
	public int delete( AppUserDto dto , boolean requirePasswordCheck);
	
	/** 권한조회 - readAuth (로그인)  **/
	public AppUserAuthDto readAuth(String email, String provider);
	
	/** 사용자조회 (마이페이지)  **/
	public AppUserDto  selectEmail(String email, String provider);
	
	/** 중복체크 **/
	public int iddouble(String email, String provider);
	
	/** 비밀번호검증  **/
	public boolean matchesPassword(  String email, String provider, String rawPassword );
}
