package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.filter.MyFilter3;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CorsFilter corsFilter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		// 시큐리티 필터 체인이 내가 생성한 필터보다 먼저 실행된다.
		// 가장 빨리 필터를 실행하고싶다면 SecurityContextPersistenceFilter addFilterBefore로 설정하면 된다
		http.addFilterAfter(new MyFilter3(), BasicAuthenticationFilter.class);
		
		http.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.addFilter(corsFilter); // @CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O)
		
		http.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);
			
		http.authorizeHttpRequests(req -> req
				.requestMatchers("/api/v1/user/**")
				.hasAnyRole("USER", "MANAGER", "ADMIN")
				.requestMatchers("/api/v1/manager/**")
				.hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/api/v1/admin/**")
				.hasRole("ADMIN")
				.anyRequest().permitAll());
		
		return http.getOrBuild();
	}
}
