package com.example.springsocial;

import com.example.springsocial.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SpringSocialApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(SpringSocialApplication.class, args);
	}
	@Bean
	public WebMvcConfigurer customConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
				configurer.defaultContentType(MediaType.APPLICATION_JSON);
			}
		};
	}
} 
 