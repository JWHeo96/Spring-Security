package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인을 진행이 완료가 되면 session을 만들어줍니다. (Security ContextHolder)
// 오브젝트 타입 => Authentication 타입 객체
// Authentication 안에 User정보가 있어야 됨.
// User 오브젝트타입 => UserDetails 타입 객체 

// Security Session => Authentication => UserDetails(PrincipalDetails)

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
	
	private User user; // 콤포지션
	private Map<String, Object> attributes;
	
	// 일반 로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(() -> user.getRole());
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	public boolean isAccountNonExpired() { // 만료
		return true;
	}

	public boolean isAccountNonLocked() { // 잠김
		return true;
	}

	public boolean isCredentialsNonExpired() { // 비밀번호 만료
		return true;
	}

	public boolean isEnabled() { // 활성화
		
		// 우리 사이트!! 1년동안 회원이 로그인을 안하면!! 휴면 계정으로 하기로 함.
		// 현재시간 - 로그인시간 => 1년을 초과하면 return false;
		return true;
	}

	@Override
	public String getName() {
		return null;
	}
	
	/*
	 * @Override public <A> A getAttribute(String name) { return
	 * OAuth2User.super.getAttribute(name); }
	 */
	@Override
	public Map<String, Object> getAttribute(String name) {
		return attributes;
	}

}
