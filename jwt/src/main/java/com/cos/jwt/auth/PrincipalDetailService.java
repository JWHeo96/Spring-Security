package com.cos.jwt.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// http://localhost:8080/login 요청 시 동작
@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {
	
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("PrincipalDetailService ===> loadUserByUsername()");
		User userEntity = userRepository.findByUsername(username);
		
		System.out.println("userEntity : " + userEntity);
		
		return new PrincipalDetails(userEntity);
	}

}
