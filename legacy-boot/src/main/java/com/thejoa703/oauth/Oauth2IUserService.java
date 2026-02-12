package com.thejoa703.oauth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.thejoa703.dao.AppUserDao;
import com.thejoa703.dto.AppUserAuthDto;
import com.thejoa703.dto.AppUserDto;
import com.thejoa703.dto.AuthDto;
import com.thejoa703.security.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Oauth2IUserService extends DefaultOAuth2UserService{ 
	@Autowired AppUserDao dao;
	@Autowired PasswordEncoder  passwordEncoder;
	
	@Override public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("1: " + userRequest);
		System.out.println("........." + userRequest);
		System.out.println(userRequest);
		
		OAuth2User  oAuth2User = super.loadUser(userRequest);  //로그인정보
		String provider  = userRequest.getClientRegistration().getRegistrationId();
		
		UserInfoOAuth2  info;
		if("google".equals(provider)) {
			info = new UserInfoGoogle( oAuth2User.getAttributes());
		}else if("kakao".equals(provider)) {
			info = new UserInfoKakao( oAuth2User.getAttributes());
		}else if("naver".equals(provider)) {
			info = new UesrInfoNaver( oAuth2User.getAttributes());
			System.out.println("....... naver : " + info);
		}else {
			throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 provider : " + provider);
		}
		
		String email = info.getEmail();
		String nickname = info.getNickname();
		String providerId = info.getProviderId();
		
		///////  유저 있는지확인 
		AppUserDto      userParam    = new  AppUserDto(); 
		userParam.setEmail(email);
		userParam.setProvider(provider); 
		
		AppUserDto      user    = dao.findByEmail(userParam);  //유저정보가져오기 
		
		if(user == null) { //회원가입 ##
			user = new AppUserDto();
			user.setEmail(email);
			user.setNickname(nickname  !=null &&  !nickname.isBlank() ? nickname : "사용자");
			user.setProvider(provider);
			user.setProviderId(providerId);
			user.setPassword(  passwordEncoder.encode( UUID.randomUUID().toString()));  //인코딩적용
			user.setMbtiTypeId(1);
			
			dao.insertAppUser(user);
			
			AuthDto auth = new AuthDto();
			auth.setEmail(email);
			auth.setAuth("ROLE_MEMBER");
			dao.insertAuth(auth);
			System.out.println("........... 신규 소셜 사용자가입" + email);
		}else { //업데이트 ##
			if( nickname != null  &&   !nickname.isBlank()   ) {
				user.setNickname(nickname);
				dao.updateAppUser(user);
				System.out.println("........... 소셜 사용자 업데이트" + email);
			}
		}
		
  		AppUserAuthDto  authDto = dao.readAuthByEmail(userParam);  //유저권한확인 
		CustomUserDetails   cusomUserDetails = new CustomUserDetails(user, authDto); 
		
		//##
		Map<String, Object> attrs = new HashMap<>(oAuth2User.getAttributes());
		attrs.put("provider", provider);
		attrs.put("email"   , email);
		attrs.put("nickname", nickname);
		cusomUserDetails.setAttributes(attrs);
		
		return cusomUserDetails;
	} 
}













