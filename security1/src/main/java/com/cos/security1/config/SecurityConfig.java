package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableMethodSecurity(securedEnabled = true) // secured 어노테이션 활성화, PreAuthorize 어노테이션 활성화 
public class SecurityConfig {
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	// 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	/*
	 * @Bean public BCryptPasswordEncoder encodePwd() { return new
	 * BCryptPasswordEncoder(); }
	 */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(AbstractHttpConfigurer::disable);
		
		http.authorizeHttpRequests(req -> req
				.requestMatchers("/user/**").authenticated() // 인증만 되면 들어갈 수 있는 주소
				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().permitAll()
			);
		
		http.formLogin(form -> form
				.loginPage("/loginForm")
				.loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다.
				.defaultSuccessUrl("/user") // 이전 요청에 대한 url을 보관하여 보내줌
			);
		
		// 1.코드받기(인증)
		// 2. 엑세스토큰(권한)
		// 3. 사용자프로필 정보를 가져오고 
		// 4-1. 그 정보를 토대로 회원강비을 자동으로 진행시키기도 함
		// 4-2. (이메일, 전화번호, 이름, 아이디) 쇼핑몰 -> (집주소)
		http.oauth2Login(login -> login
				.loginPage("/loginForm") 
				.defaultSuccessUrl("/user")
				.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint 
						.userService(principalOauth2UserService)) // 구글 로그인이 완료된 뒤의 후처리가 필요함. Tipe. 코드X, (엑세스토큰+사용자프로필정보 O)
			); 
		
		
		return http.getOrBuild();
	}
}
