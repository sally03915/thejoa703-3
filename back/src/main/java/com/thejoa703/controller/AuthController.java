package com.thejoa703.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thejoa703.dto.request.LoginRequest;
import com.thejoa703.dto.request.UserRequestDto;
import com.thejoa703.dto.response.UserResponseDto;
import com.thejoa703.security.JwtProperties;
import com.thejoa703.security.JwtProvider;
import com.thejoa703.security.TokenStore;
import com.thejoa703.service.AppUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 인증/사용자 관리 컨트롤러
 * - 회원가입, 로그인, 닉네임변경, 프로필이미지 업로드, 삭제 
 * - JWT + Redis 기반 토큰 발급/재발급/로그아웃 포함
 **/
 
@Tag(name = "Auth", description = "회원 인증 관련 API (Oracle 호환)")   //Swagger 태그
@RestController  // REST API 컨트롤러 선언
@RequestMapping("/auth") //   기본URL   /auth
@RequiredArgsConstructor //   final 필드가 자동생성자 주입
public class AuthController {

    private final JwtProvider jwtProvider;        // JWT 토큰 생성/검증 제공 (access Token /refresh Token)
    private final TokenStore tokenStore;          // Refresh Token 저장소  (Redis)
    private final JwtProperties props;            // JWT 설정값  ( 만료시간 등 )
    private final AppUserService appUserService;  // 사용자 서비스 계층

    // ✅ 회원가입
    @Operation(summary = "회원가입")   //Swagger 문서 설명
    @PostMapping( value="/signup"  , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public   ResponseEntity<UserResponseDto> singup(
    		@ModelAttribute UserRequestDto  request,
    		@RequestPart(name="ufile" , required= false) MultipartFile ufile
    		
    	){
    		return  ResponseEntity.ok(   appUserService.signup(request, ufile)  );
    }

    // 로그인
    @Operation(summary = "로그인 (Access Token + Refresh Token 발급)")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response   // 응답객체 (쿠키 설정)
    ) {
    		// 사용자 인증처리
        UserResponseDto user = appUserService.login(request);

        // Access Token 생성 ( 사용자id + 역할)
        String accessToken = jwtProvider.createAccessToken(
                user.getId().toString(),
                Map.of("role", user.getRole())
        );

        // RfreshToken Token 생성 
        String refreshToken = jwtProvider.createRefreshToken(user.getId().toString());

        // Redis Token 저장소에 저장
        tokenStore.saveRefreshToken(
                user.getId().toString(),
                refreshToken,
                (long) props.getRefreshTokenExpSeconds()
        );
        
        // 쿠키설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)  // js 접근불가
                .secure(true)  // https 전송한 허용
                .sameSite("Strict")  // csrf 방지
                .path("/")  // 전체경로 적용
                .maxAge(props.getRefreshTokenExpSeconds())  // 만료시간 설정
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // 사용자 정보반환
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "user", user
        ));
    }
    
    // 현재 로그인한 사용자 정보조회
    @Operation(summary = "현재 로그인한 사용자 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(HttpServletRequest request,
                 @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try { 
        		// Authorization 헤더에서 Access Token 확인
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);  // Bearer  제거
                var claims = jwtProvider.parse(token).getBody();  // 토큰파싱
                String userId = claims.getSubject(); // 사용자 id추출
                UserResponseDto user = appUserService.findById(Long.valueOf(userId)); //사용자조회
                return ResponseEntity.ok(user);
            } 
            //  Authorization 없으면  RefreshToken 쿠키를 확인
            if (refreshToken != null) {
                var claims = jwtProvider.parse(refreshToken).getBody();
                String userId = claims.getSubject();// 사용자 id추출
                UserResponseDto user = appUserService.findById(Long.valueOf(userId)); //사용자조회
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(401).build();  // 인증실패 401
        } catch (Exception e) {
            return ResponseEntity.status(401).build();  // 예외 발생시 인증 실패
        }
    }
    // 닉네임
    @Operation(summary = "닉네임 변경")
    @PatchMapping("/{userId}/nickname")  // Patch
    public ResponseEntity<UserResponseDto> updateNickname(
            @PathVariable("userId") Long userId,  // 경로에서 UserId 추출
            @RequestParam("nickname") String nickname   
    ) {
        return ResponseEntity.ok(appUserService.updateNickname(userId, nickname));
    }

    @Operation(summary = "프로필 이미지 업로드/교체")
    @PostMapping(value = "/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> updateProfileImage(
            @PathVariable("userId") Long userId,
            @RequestParam("ufile") MultipartFile ufile
    ) {
        return ResponseEntity.ok(appUserService.updateProfileImage(userId, ufile));
    }
    ///////////////////////////
    /*
    //  사용자 삭제 (soft delete)
    @Operation(summary = "사용자 삭제(soft delete)")
    @DeleteMapping
    public ResponseEntity<Void> deleteByEmail(@RequestParam("email") String email) {
        appUserService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }*/
     
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try {
        		
        		// AccessToken 확인
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            } 
            // AccessToken 추출
            String accessToken = authHeader.substring(7);
            var claims = jwtProvider.parse(accessToken).getBody();
            String userId = claims.getSubject();

            // 해당하는 유저삭제
            appUserService.deleteById(Long.valueOf(userId));

            // refresh 토큰삭제
            if (refreshToken != null) {
                tokenStore.deleteRefreshToken(userId);
            }
            // 쿠키에서 삭제
            ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(0)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }


    @Operation(summary = "전체 사용자 수 조회")
    @GetMapping("/count")
    public ResponseEntity<Long> countUsers() {
        return ResponseEntity.ok(appUserService.countUsers());
    }

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(appUserService.existsByEmail(email));
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(appUserService.existsByNickname(nickname));
    }

    @Operation(summary = "Access Token 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@CookieValue("refreshToken") String refreshToken) {
        var claims = jwtProvider.parse(refreshToken).getBody();
        String userId = claims.getSubject();

        String stored = tokenStore.getRefreshToken(userId);
        if (stored == null || !stored.equals(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        }

        String role = appUserService.findRoleByUserId(Long.valueOf(userId));

        String newAccessToken = jwtProvider.createAccessToken(
                userId,
                Map.of("role", role)
        );

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
    // ✅ 로그아웃
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("refreshToken") String refreshToken,
                                       HttpServletResponse response) {
        var claims = jwtProvider.parse(refreshToken).getBody();
        String userId = claims.getSubject();

        tokenStore.deleteRefreshToken(userId);
 
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.noContent().build();
    }
}
