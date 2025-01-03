package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.config.jwt.JwtAuthorizationFilter;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsFilter corsFilter;
	private final UserRepository userRepository;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
		
		// 시큐리티 필터 체인이 내가 생성한 필터보다 먼저 실행된다.
		// 가장 빨리 필터를 실행하고싶다면 SecurityContextPersistenceFilter addFilterBefore로 설정하면 된다
		//http.addFilterBefore(new MyFilter3(), SecurityContextHolderFilter.class);

		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilter(corsFilter); // @CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O)

		http.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable);

		http
			.addFilter(new JwtAuthenticationFilter(authenticationManager)) // AuthenticationManager
			.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository)); // AuthenticationManager

		http.authorizeHttpRequests(req -> req.requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
				.requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/api/v1/admin/**").hasRole("ADMIN").anyRequest().permitAll());

		return http.getOrBuild();
	}
}
