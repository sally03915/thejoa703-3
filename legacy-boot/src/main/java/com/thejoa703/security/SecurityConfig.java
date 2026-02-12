package com.thejoa703.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.thejoa703.oauth.Oauth2IUserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor  //##
public class SecurityConfig { 
    
    private final Oauth2IUserService oauth2IUserService; //##
    
    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http /* 1. 허용경로 */
            .authorizeHttpRequests(auth -> auth
                // 누구나 접근 가능
                .antMatchers("/users/join", "/users/login", "/users/iddouble", "/images/**", "/api/**").permitAll()
                // 로그인한 유저들만 접근 가능
                .antMatchers("/users/mypage", "/users/update", "/users/delete").authenticated()
                // 그 외 요청은 모두 허용
                .anyRequest().permitAll()
            )
            /* 2. 로그인 처리 */
            .formLogin(form -> form
                .loginPage("/users/login")              // ← 로그인폼
                .loginProcessingUrl("/users/loginProc") // ← 로그인경로
                .defaultSuccessUrl("/users/mypage", true) // ← 로그인성공시 경로
                .failureUrl("/users/fail")              // ← 로그인 실패시 경로
                .permitAll()
            ) 
            /* 3. 로그아웃 */
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout")) // ← 로그아웃경로
                .logoutSuccessUrl("/users/login")                                // ← 로그아웃성공시
                .invalidateHttpSession(true)                                     // ← 세션 다 지우기
                .permitAll()
            )
            /* 4. OAuth2 로그인 */
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/users/login")                 // 로그인 폼
                .defaultSuccessUrl("/users/mypage", true)  // 로그인 성공시 경로
                .authorizationEndpoint(endpoint -> 
                    endpoint.baseUri("/oauth2/authorization")) // ← 소셜 로그인 진입 경로
                .redirectionEndpoint(endpoint -> 
                    endpoint.baseUri("/login/oauth2/code/*"))  // ← 콜백 경로
                .userInfoEndpoint(userInfo -> userInfo.userService(oauth2IUserService))
            );

        // CSRF 예외처리 제거 → 폼에서 CSRF 토큰 사용
        return http.build();  
    }
     
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager(); 
    }
}
