package com.thejoa703.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.thejoa703.dao.AppUserDao;
import com.thejoa703.dto.AppUserAuthDto;
import com.thejoa703.dto.AppUserDto;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired  AppUserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// username    "1@1:local,  1@1:kakao ,,,,,"  형태
		String [] parts = username.split(":");
		String email = parts[0];
		String provider = parts.length > 1? parts[1] : "local";
		
		AppUserDto param = new AppUserDto();
		param.setEmail(email);
		param.setProvider(provider);
		
		AppUserAuthDto authDto = userDao.readAuthByEmail(param);
		if( authDto == null ) {  throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);   }
		
		AppUserDto user = userDao.findByEmail(param);
		if( user == null ) {  throw new UsernameNotFoundException("사용자 기본정보를 찾을 수 없습니다: " + username);   }
		return  new CustomUserDetails(user, authDto); //사용자정보, 사용자로그인정보
	}
}

