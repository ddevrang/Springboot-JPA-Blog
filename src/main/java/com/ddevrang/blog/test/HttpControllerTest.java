package com.ddevrang.blog.test;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 사용자가 요청 -> 응답(HTML 파일)
// @Controller

// 사용자가 요청 -> 응답(Data)
@RestController
public class HttpControllerTest {
	
	private static final String TAG = "HttpController Test : ";
	
	@GetMapping("/http/lombok")
	public String lombokTest() {
//		Member m = new Member(1, "ted", "123123", "email");
//		System.out.println(TAG+"getter : "+m.getId());
//		m.setId(5000);
//		System.out.println(TAG+"getter : "+m.getId());
		
		Member m = new Member.MemberBuilder().username("tan").password("1212").email("tan@m.m").build();
		System.out.println(TAG+"getter : "+m.getUsername());
		m.setUsername("cos");
		System.out.println(TAG+"getter : "+m.getUsername());
		
		return "lombok test 완료";
	}
	
	// http://localhost:8080/http/get (select)
	@GetMapping("/http/get")
	//public String getTest(@RequestParam int id, @RequestParam String username) {
	public String getTest(Member m) {
		return "get 요청 : "+m.getId()+", "+m.getUsername()+", "+m.getPassword()+", "+m.getEmail();
	}

	// http://localhost:8080/http/post (insert)
	@PostMapping("/http/post")
	// x-www-form-urlencoded 방식
//	public String postTest(Member m) {
//		return "post 요청 : "+m.getId()+", "+m.getUsername()+", "+m.getPassword()+", "+m.getEmail();
//	}
	// raw 방식 text/plain
//	public String postTest(@RequestBody String text) {
//		return "post 요청 : "+text;
//	}
	// raw 방식 application/json
	public String postTest(@RequestBody Member m) {
		return "post 요청 : "+m.getId()+", "+m.getUsername()+", "+m.getPassword()+", "+m.getEmail();
	}

	// http://localhost:8080/http/put (update)
	@PutMapping("/http/put")
	public String putTest() {
		return "put 요청";
	}

	// http://localhost:8080/http/delete (delete)
	@DeleteMapping("/http/delete")
	public String deleteTest() {
		return "delete 요청";
	}
}
