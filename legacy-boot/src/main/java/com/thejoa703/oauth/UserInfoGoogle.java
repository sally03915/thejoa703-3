package com.thejoa703.oauth;

import java.util.Map;
import lombok.AllArgsConstructor; 

@AllArgsConstructor 
public class UserInfoGoogle  implements UserInfoOAuth2{ 
	private final Map<String, Object> attributes;
	
	@Override public String getProvider() { return "google"; }

	@Override public String getProviderId() {
		Object sub  = attributes.get("sub");
		return sub != null? sub.toString() : null;
	}

	@Override public String getEmail() {
		Object email  = attributes.get("email");
		return email != null? email.toString() : null;
	}

	@Override
	public String getNickname() {
		Object name  = attributes.get("name");
		return name != null? name.toString() : null;
	}

	@Override
	public String getImage() {
		Object picture  = attributes.get("picture");
		String image    = picture != null? picture.toString(): null;
		return image != null? image : "no.png";
	}

}
