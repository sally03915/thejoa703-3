package com.thejoa703.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	// applcation.yml 에서 업로드된 경로 불러오기
    @Value("${file.upload-dir}")    
    private String uploadDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { 
        registry.addResourceHandler("/uploads/**")    //   /uploads/** 요청을 
                .addResourceLocations("file:" + uploadDir + "/");  // 실제 uploads 폴더와 매핑
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) { 
    		// 모든 경로에서 CORS 허용
        registry.addMapping("/**")
                .allowedOrigins("*") // 필요 시 특정 도메인으로 제한 가능
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
