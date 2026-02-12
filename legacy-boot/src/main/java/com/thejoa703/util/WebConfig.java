package com.thejoa703.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	@Value("${upload.path}") private String uploadPath;     //    /upload/ 경로요청    
	@Value("${resource.path}") private String resourcePath; //    C:/upload
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) { 
		registry.addResourceHandler(uploadPath)
		        .addResourceLocations("file:" + resourcePath + "/" );
	}

}
