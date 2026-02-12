package com.thejoa703.oauth;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserInfoKakao implements UserInfoOAuth2 {

	private final Map<String, Object> attributes;

	@Override public String getProvider() { return "kakao"; }

	@Override
	public String getProviderId() { 
		Object id= attributes.get("id");
		return id != null ? id.toString() : null;
	}
	
	@SuppressWarnings("unchecked")
	private  Map<String, Object>  getAccount(){
		Object account = attributes.get("kakao_account");
		return account  instanceof Map? (Map<String, Object>)account : null;
	}
 
	@SuppressWarnings("unchecked")
	private  Map<String, Object>  getProfile(){
		Map<String, Object> account = getAccount(); 
		
		if(account != null) {
			Object profile = account.get("profile");
			return  profile  instanceof Map? (Map<String, Object>)profile : null;
		} 
		return  null;
	}
	
	@Override public String getEmail() {
		Map<String, Object> account = getAccount(); 
		return account != null ?  (String) account.get("email") : null ;
	}

	@Override
	public String getNickname() {
		// kakao_account.profile.nickname
		Map<String, Object> profile = getProfile(); 
		if( profile != null   && profile.get("nickname") !=null ) {
			return (String)profile.get("nickname"); 
		}
		//properties = {nickname=효정}
		Object props = attributes.get("properties");
		if( props instanceof Map ) { return (String)((Map<String, Object>)props).get("nickname"); }
		return null;
	}

	@Override
	public String getImage() { 
		Map<String, Object> profile = getProfile(); 
		if( profile != null   && profile.get("thumbnail_image_url") !=null ) {
			return (String)profile.get("thumbnail_image_url"); 
		} 
		
		Object props = attributes.get("properties");
		if( props instanceof Map ) { 
			String image=(String)((Map<String, Object>)props).get("thumbnail_image");
			return  image!=null?   image : "no.png"; 
		} 
		return "no.png";
	} 
}
