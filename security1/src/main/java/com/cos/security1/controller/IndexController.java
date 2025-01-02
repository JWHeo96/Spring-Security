package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller // View 리턴
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
			Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) { // DI(의존성 주입)
		// 세션의 인증정보를 가져오는 두 가지 방법 
		// 1. authentication 객체 다운캐스팅 후 조회
		System.out.println("/test/login ====================");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication : " + principalDetails.getUser());
		
		// 2. AuthentiactionPrincipal 어노테이션 활용
		System.out.println("userDetails : " + userDetails.getUser());
		
		return "세선 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { // DI(의존성 주입)
		System.out.println("/test/oauth/login ====================");
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication : " + oauth2User.getAttributes());
		
		System.out.println("oauth2User : " + oauth.getAttributes());
		
		return "OAuth 세선 정보 확인하기";
	}
	
	@GetMapping({"", "/"})
	public String index() {
		// 머스테치 기본 폴더 src/main/resources/
		// 뷰리졸버 설정 : templates (prefix) .mustache (suffix) 생략 가능
		return "index"; // src/main/resources/templates/index.mustache
	}
	
	@GetMapping("/user")
	public @ResponseBody User user(@AuthenticationPrincipal PrincipalDetails principlaDetails) {
		System.out.println("principalDetails : " + principlaDetails.getUser());
		return principlaDetails.getUser();
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	// 스프링 시큐리티가 해당 주소를 낚아챔 - SecurityConfig 파일 생성 후 작동안함
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		
		user.setRole("ROLE_USER");
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user); // 회원가입 잘됨. 비밀번호 1234 => 시큐리티로 로그인을 할 수없음. 이유는 패스워드가 암호화가 안되었기 때문에
		
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
}
