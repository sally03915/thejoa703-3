package com.thejoa703.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.thejoa703.dto.request.LoginRequest;
import com.thejoa703.dto.request.UserRequestDto;
import com.thejoa703.dto.response.UserResponseDto;
import com.thejoa703.entity.AppUser;
import com.thejoa703.repository.AppUserRepository;
import com.thejoa703.util.FileStorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  //##
@Transactional   //org.springframework.transaction.annotation.Transactional
public class AppUserService {

	private final AppUserRepository   appUserRepository;  //##
	private final FileStorageService  fileStorageService; //##
	private final PasswordEncoder      passwordEncoder;     //##
	
	private static final String DEFAULT_PROFILE_IMAGE="uploads/default.png"; 
	
	// Create: 회원가입
    public UserResponseDto signup(UserRequestDto request, MultipartFile profileImage) {
    		// provider 값이 없으면 기본값을 "local" 사용
        String provider = request.getProvider() != null ? request.getProvider() : "local";
        
        // 이메일 + provider 중복검사
        if (appUserRepository.findByEmailAndProvider(request.getEmail(), provider).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 닉네임 중복 검사 
        if (appUserRepository.countByNickname(request.getNickname()) > 0) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        // 새로운 사용자 엔티티 생성
        AppUser user = new AppUser();
        user.setEmail(request.getEmail());  //이메일설정
        user.setPassword(passwordEncoder.encode(request.getPassword()));  //비밀번호설정
        user.setNickname(request.getNickname());  //닉네임설정
        user.setProvider(provider);  // provider( local, google, naver, kakao,,, 등)
        user.setRole("ROLE_USER");  //기본권한
        user.setUfile(profileImage != null && !profileImage.isEmpty()
                ? fileStorageService.upload(profileImage)  //  업로드된 이미지 저장
                : DEFAULT_PROFILE_IMAGE);  // 기본프로필 이미지 사용
        //////////////////////////////////////////////// DB저장후  DTO 반환
        return UserResponseDto.fromEntity(appUserRepository.save(user));
    }	
	 
	
	// Read  : 로그인
    public UserResponseDto login(LoginRequest request) {
    		// DB정보 : 이메일+provider로 사용자 조회, 없으면 예외
        AppUser user = appUserRepository.findByEmailAndProvider(
                request.getEmail(),
                request.getProvider() != null ? request.getProvider() : "local"
        ).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        
        // 비밀번호 검증                사용자가 입력한값         / DB의 비밀번호
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        return UserResponseDto.fromEntity(user);
    }
    
    
    
	// Read  : 사용자조회  by email + provider
    public Optional<AppUser> findByEmailAndProvider(String email, String provider) {
        return appUserRepository.findByEmailAndProvider(email, provider);
    }
    
	// Read  : 사용자조회  by Id
    public UserResponseDto findById(Long userId) {
        AppUser user = appUserRepository.findById(userId)
        							.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return UserResponseDto.fromEntity(user);
    }    
    
	// Update : 닉네임 변경
    public UserResponseDto updateNickname(Long userId, String newNickname) {
    		// 닉네임 중복검사
        if (appUserRepository.countByNickname(newNickname) > 0) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        // 사용자조회 후 
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));  //##1. 조회
        user.setNickname(newNickname); //##2. 닉네임셋팅
        return UserResponseDto.fromEntity(appUserRepository.save(user));  //##3. save 변경 저장후 dto반환
    }    
    
	// Update : 프로필 이미지변경
    public UserResponseDto updateProfileImage(Long userId, MultipartFile profileImage) {
    		// 사용자 조회
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        // 새이미지 업로드 또는 기본 이미지 설정
        user.setUfile(profileImage != null && !profileImage.isEmpty()   // 이미지가 빈게 아니라면
                ? fileStorageService.upload(profileImage)               // 업로드
                : DEFAULT_PROFILE_IMAGE);                               // 기본값
        return UserResponseDto.fromEntity(appUserRepository.save(user)); // 저장 후 dto반환
    }
    
	// Delete : ID 삭제
	public void deleteById(Long userId) {  appUserRepository.deleteById(userId);  }
	
	// 전체 사용자수
	public long countUsers() {  return  appUserRepository.count(); }
	
	// 이메일 중복 여부
	public boolean existsByEmail(String email) {  return  appUserRepository.countByEmail(email) > 0 ;}
	
	// 닉네임 중복 여부
	public boolean existsByNickname(String nickname) {  return  appUserRepository.countByNickname(nickname) > 0 ;}
	
	
	
	
	// 소셜 사용자 저장
	public AppUser saveSocialUser( String email, String provider, String providerId, String nickname, String image) {
		AppUser user  = AppUser.builder()
							   .email(email)
							   .provider(provider)
							   .providerId(providerId)
							   .nickname(nickname)
							   .ufile(image)
							   .role("ROLE_USER")
							   .build();
		return appUserRepository.save(user);  // CREATE → insert
	}
	
	
	// 권한 조회
	public String findRoleByUserId(Long userId) {
		return   appUserRepository.findById(userId)       // userId 로 사용자 조회   Optional<AppUser> 반환
								  .map(AppUser::getRole)  // 조회된 사용자가 있으면 getRole() 권한반환
								  .orElse("ROLE_USER");   // 없으면 기본값 ROLE_USER 반환
	}
	
}
 


