package com.ddevrang.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.ddevrang.blog.config.auth.PrincipalDetail;
import com.ddevrang.blog.model.KakaoProfile;
import com.ddevrang.blog.model.OAuthToken;
import com.ddevrang.blog.model.User;
import com.ddevrang.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// 인증이 안 된 사용자들이 출입할 수 있는 경로를 /auth의 하위경로만 허용
// 그냥 주소가 /이면 index.jsp 허용
// static 폴더 하위에 있는 /js, /css, /image 폴더들은 허용

@Controller
public class UserController {
	
	@Value("${ddevrang.key}")
	private String ddevrangKey;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@GetMapping("/auth/joinForm")
	public String joinForm() {

		return "user/joinForm";
	}

	@GetMapping("/auth/loginForm")
	public String loginForm() {

		return "user/loginForm";
	}

	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) { 		// 리턴 값에 String에 @ResponseBody를 붙이면 Data를 리턴해주는 컨트롤러 함수(현재는 지움.) = @RestController

		// POST 방식으로 key=value 데이터를 요청(카카오에 요청)
		// http통신을 하는데는 Retrofit2, OkHttp, RestTemplate 등의 방식이 있음. 그 중 RestTemplate이
		// 편리함.
		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "-");
		params.add("client_id", "-");
		params.add("redirect_uri", "-");
		params.add("code", code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담는다.
		// RestTemplate의 exchange함수가 HttpEntity 오브젝트를 넣게끔 되어있기 때문에 필요함.
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		// Http 요청하기(Post 방식) - 그리고 response 변수의 응답을 받음.
		ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoTokenRequest, String.class);

		// response.getBody()에 현재 액세스 토큰에 대한 응답 데이터가 JSON으로 담겨있는데,
		// 자바에서 처리하기 쉽도록 라이브러리를 통해 JSON데이터를 Object에 담을 예정. (JSON -> Object로 변환)
		// 지금은 ObjectMapper를 사용. 쉽고, 내장되어있다. (Gson, Json Simple을 사용해서도 처리 가능하다.)
		ObjectMapper objectMapper = new ObjectMapper();

		OAuthToken oauthToken = null;

		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());

		RestTemplate rt2 = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담는다.
		// RestTemplate의 exchange함수가 HttpEntity 오브젝트를 넣게끔 되어있기 때문에 필요함.
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = new HttpEntity<>(headers2);

		// Http 요청하기(Post 방식) - 그리고 response 변수의 응답을 받음.
		ResponseEntity<String> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoProfileRequest2, String.class);

		ObjectMapper objectMapper2 = new ObjectMapper();

		KakaoProfile kakaoProfile = null;

		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// User 오브젝트 : username, password, email
		System.out.println("카카오 아이디(시퀀스) : " + kakaoProfile.getId());
		System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());

		System.out.println("블로그서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId());
		System.out.println("블로그서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());

		System.out.println("블로그서버 패스워드 : " + ddevrangKey);

		User kakaoUser = User.builder()
				.username(kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId())
				.password(ddevrangKey).
				email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();

		// 가입자, 비가입자를 체크하여 분기처리
		User originUser = userService.회원찾기(kakaoUser.getUsername());

		// 비가입자인 경우 회원가입을 진행.
		// originUser는 결과가 없어도 빈 객체를 리턴하기때문에 getUsername()의 값을 확인해야 함.
		if (originUser.getUsername() == null) {
			System.out.println("기존 회원이 아니기에 자동 회원가입을 진행합니다.");
			userService.회원가입(kakaoUser); 
		}
		
		// 로그인 처리
		System.out.println("자동 로그인을 진행합니다.");
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), ddevrangKey));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "redirect:/";
	}

	@GetMapping("/user/updateForm")
	public String updateForm(@AuthenticationPrincipal PrincipalDetail principal) {

		return "user/updateForm";
	}

}
