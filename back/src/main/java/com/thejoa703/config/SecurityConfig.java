package com.thejoa703.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.thejoa703.oauth2.OAuth2SuccessHandler;
import com.thejoa703.security.JwtAuthenticationFilter;
import com.thejoa703.security.JwtProvider;

import lombok.RequiredArgsConstructor;


/**
 * Spring Security 설정
 * - CSRF/FormLogin/HttpBasic 비활성화
 * - 세션설정을 Stateless로 설정  (Jwt 인증 기반)
 * - Jwt 인증 필터를 UsernamePasswordAuthenticationFilter 추가
 * - Cors 설정포함 
 * */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http 
        		// 기본 보안 기능 비활성화
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 세션을 STATELESS
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 권한 설정
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers(HttpMethod.OPTIONS , "/**").permitAll()  //#####
                // Swagger, 인증 관련 경로는 모두 허용  ------ 점검 ; uploads
                .requestMatchers(
                    "/auth/**", "/login/**", "/oauth2/**",
                    "/swagger-ui/**", "/v3/api-docs/**",
                    "/swagger-resources/**", "/webjars/**",
                    "/configuration/**", "/uploads/**"  , "/api/deptusers/**" , "/api/likes/**"
                ).permitAll()
                // 전체조회만 허용
                .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()   
                // 단건조회만 허용
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()  
                // 해쉬태크
                .requestMatchers(HttpMethod.GET, "/api/posts/search/hashtag").permitAll()  
                .requestMatchers("/api/posts/paged").permitAll() 
                // /api/요청은 JWT 인증 필요
                .requestMatchers("/api/**").authenticated()
                // 나머지는 모두 허용
                .anyRequest().permitAll()
            )
            // Oauth2 로그인은 소셜로그인 전용
            .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2SuccessHandler))
            // JWT 필터 추가
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 												개발환경 					운영환경(nginx 통해서 접근하는 실제주소)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000" , "http://52.79.175.214"));  //★ Front 포트번호

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); //
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
