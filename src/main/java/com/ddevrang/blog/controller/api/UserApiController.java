package com.ddevrang.blog.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ddevrang.blog.dto.ResponseDto;
import com.ddevrang.blog.model.User;
import com.ddevrang.blog.service.UserService;

@RestController
public class UserApiController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	// 해당 객체를 스프링 컨테이너 Bean에 등록 (DI를 활용하는 방식)
	// 로그인 방식을 스프링 시큐리티 방식으로 변경하며 주석처리함.
	/*
	 * @Autowired private HttpSession session;
	 */

	@PostMapping("/auth/joinProc")
	public ResponseDto<Integer> save(@RequestBody User user) {

		System.out.println("UserApiController : save 호출됨");

		int result = userService.회원가입(user); // 1이면 성공, -1이면 실패

		return new ResponseDto<Integer>(HttpStatus.OK.value(), result); // 자바오브젝트를 JSON으로 변환해서 return (Jackson 라이브러리 기능)
	}

	@PutMapping("/user")								// Json 데이터로 받고싶으면 @RequestBody를 붙여야 함.
	public ResponseDto<Integer> update(@RequestBody User user){
		
		userService.회원수정(user);
		
		// 여기서는 트랜잭션이 종료되기 때문에 DB값은 변경되었음.
		// 하지만 세션 값은 변경되지 않은 상태이기 때문에 우리가 직접 세션값을 변경해줄 것임.
		
		// 세션등록 (authenticationManager를 통해 접근하는 방법)
		Authentication authentication 
					= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		
// 아래의 방법처럼 직접 SPRING_SECURITY_CONTEXT에 직접 접근하여 세션 만드는것이 안되는듯 함. (실패함 -> username 이슈가 있었어서 다시해보면 될수도?) 
//		Authentication authentication = 
//				new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());		// 토큰을 만들어줌.
//		SecurityContext securityContext = SecurityContextHolder.getContext();		// 시큐리티 컨텍스트 생성 및 접근
//		securityContext.setAuthentication(authentication);		
//		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		
		
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	}

	// 아래의 방법은 전통적인 로그인 방식 (스프링 시큐리티의 로그인 방식으로 변경예정 => 주석처리)
	/*
	 * @PostMapping("/api/user/login") public ResponseDto<Integer>
	 * login(@RequestBody User user, HttpSession session){
	 * 
	 * System.out.println("UserApiController : login 호출됨");
	 * 
	 * User principal = userService.로그인(user); // principal : 접근주체
	 * 
	 * if(principal != null) { session.setAttribute("principal", principal); // 세션을
	 * 만들어줌 }
	 * 
	 * return new ResponseDto<Integer>(HttpStatus.OK.value(), 1); }
	 */

}
