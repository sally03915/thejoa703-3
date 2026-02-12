package com.thejoa703.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thejoa703.dto.AppUserDto;
import com.thejoa703.security.CustomUserDetails;
import com.thejoa703.service.AppUserService;

@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private AppUserService userService;
	
	/* 회원가입 */	
	//http://localhost:8484/boot001/users/iddouble?email=1@1&provider=local
	@PreAuthorize("permitAll()")
	@RequestMapping("/iddouble")
	@ResponseBody
	public Map<String, Object>  iddouble( @RequestParam String email, @RequestParam String provider ){
		Map<String, Object>  result = new HashMap<>();
		result.put("cnt", userService.iddouble(email, provider));
		return result;
	} 
	//http://localhost:8484/boot001/users/join
	@GetMapping("/join")
	public String joinForm() {  return "users/join"; }
	
	@PostMapping("/join")
	public String join( @RequestParam(value="file" , required=false) MultipartFile file
		, AppUserDto dto , RedirectAttributes rttr ) {
		  try {	
			int result = userService.insert(file, dto);
			rttr.addFlashAttribute("successMessage" , result > 0? "회원가입 성공!" : "회원가입 실패");
			return "redirect:/users/login";
		  }catch(Exception e) { 
				rttr.addFlashAttribute("errorMessage" ,  "회원가입 실패: " +  e.getMessage());
				return "redirect:/users/join";
		  }
	}
	

	/* 로그인 : 폼 , 성공, 실패 */
	@GetMapping("/login")
	public String loginForm() { return "users/login"; }
	
	@GetMapping("/fail")
	public String loginFail(Model model) {
		model.addAttribute("errorMessage" , "로그인 실패: 아이디 또는 비밀번호를 확인하세요.");
		return "users/login";
	}
	//////////////////////////////////////////////////////////
	/* 마이페이지 */	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/mypage")
	public String mypage( Authentication   authentication  , Model model) { 
		String email = null , provider = null;
		Object principal =   authentication.getPrincipal();
		
		//1. local 
		//-  Authentication : import org.springframework.security.core.Authentication;
		//-  CustomUserDetails
		if( principal  instanceof CustomUserDetails  ) {
			CustomUserDetails  userDetails = (CustomUserDetails)principal;
			email    =  userDetails.getUser().getEmail();
			provider =  userDetails.getUser().getProvider();
		}
		//2. social
		else if( principal instanceof OAuth2User ) {
			OAuth2User  oAuth2User = (OAuth2User)principal;
			email =   (String)oAuth2User.getAttributes().get("email");
			if(authentication  instanceof OAuth2AuthenticationToken ) {
				provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
			}
		}
		AppUserDto dto = userService.selectEmail(email, provider);
		if(dto == null) {
			dto = new AppUserDto();
			dto.setEmail(email);  dto.setProvider(provider);
		} 
		model.addAttribute("dto" , dto);
		return "users/mypage"; 
	}

	//////////////////////////////////////////////////////////
	/* 회원정보수정 폼, 기능 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/update")
	public String updateForm( Authentication   authentication  , Model model) { 
		String email = null , provider = null;
		Object principal =   authentication.getPrincipal();

		if( principal  instanceof CustomUserDetails  ) {
			CustomUserDetails  userDetails = (CustomUserDetails)principal;
			email    =  userDetails.getUser().getEmail();
			provider =  userDetails.getUser().getProvider();
		} 
		AppUserDto dto = userService.selectEmail(email, provider);
		model.addAttribute("dto" , dto); 
		return "users/update"; 
	} 
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/update")
	public String update( @RequestParam(value="file" , required=false) MultipartFile file
				, AppUserDto dto ,  RedirectAttributes rttr ) {
		int result = userService.update(file, dto);
		rttr.addFlashAttribute("successMessage" , result> 0? "회원정보 수정 성공" : "회원정보 수정 실패");
		return "redirect:/users/mypage"; 
	}


	//////////////////////////////////////////////////////////
	/* 회원탈퇴 폼, 기능 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete")
	public String deleteForm(Authentication   authentication  , Model model) { 
		String email = null , provider = null;
		Object principal =   authentication.getPrincipal();

		if( principal  instanceof CustomUserDetails  ) {
			CustomUserDetails  userDetails = (CustomUserDetails)principal;
			email    =  userDetails.getUser().getEmail();
			provider =  userDetails.getUser().getProvider();
		} else if( principal instanceof OAuth2User ) {
			OAuth2User  oAuth2User = (OAuth2User)principal;
			email =   (String)oAuth2User.getAttributes().get("email");
			if(authentication  instanceof OAuth2AuthenticationToken ) {
				provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
			}
		}
		AppUserDto dto = userService.selectEmail(email, provider);
		model.addAttribute("dto" , dto); 
		return "users/delete"; 
	}
	 
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete")
	public String delete(AppUserDto dto ,  RedirectAttributes rttr 
			, Authentication   authentication , HttpServletRequest request, HttpServletResponse response ) { 
		String email = null , provider = null;
		Object principal =   authentication.getPrincipal();

		if( principal  instanceof CustomUserDetails  ) {
			CustomUserDetails  userDetails = (CustomUserDetails)principal;
			email    =  userDetails.getUser().getEmail();
			provider =  userDetails.getUser().getProvider();
		} 
		else if( principal instanceof OAuth2User ) {
			OAuth2User  oAuth2User = (OAuth2User)principal;
			email =   (String)oAuth2User.getAttributes().get("email");
			if(authentication  instanceof OAuth2AuthenticationToken ) {
				provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
			}
		}///// social
		
		dto.setEmail(email);      dto.setProvider(provider);  //##  sally03915@gmail.com  kakao
		boolean  requirePasswordCheck   = "local".equalsIgnoreCase(provider);   //social > false
		///local
		if(requirePasswordCheck) {
			if(dto.getPassword() == null ||  dto.getPassword().isEmpty()) {
				rttr.addFlashAttribute("errorMessage" , "회원탈퇴 실패: 비밀번호를 입력해주세요");
				return "redirect:/users/delete";
			} 
			if( !userService.matchesPassword(email, provider,  dto.getPassword() )) {
				rttr.addFlashAttribute("errorMessage" , "회원탈퇴 실패: 비밀번호가 일치하지 않습니다.");
				return "redirect:/users/delete";
			}
		}
		if( userService.delete(dto, requirePasswordCheck) > 0  ) {  //requirePasswordCheck = true  'local'  유저정보삭제
			Authentication  auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth != null) {  new SecurityContextLogoutHandler().logout(request, response, auth);  }
			rttr.addFlashAttribute("successMessage" , "회원탈퇴가 완료되었습니다.");
			return "redirect:/users/login";
		} else {
			rttr.addFlashAttribute("errorMessage" , "회원탈퇴 실패: 관리자에게 문의해주세요.");
			return "redirect:/users/delete";
		} 
	}

}

