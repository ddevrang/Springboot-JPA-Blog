package com.ddevrang.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddevrang.blog.model.RoleType;
import com.ddevrang.blog.model.User;
import com.ddevrang.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌. (IoC를 해준다 => 메모리에 띄워준다.)
@Service
public class UserService {

	@Autowired // DI
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional(readOnly = true)
	public User 회원찾기(String username) { 
		
		User user = userRepository.findByUsername(username).orElseGet(()->{
			return new User();
		});
		
		return user;
	}
	
	
	@Transactional // 아래의 회원가입 과정 전체를 하나의 트랜잭션으로 묶음. 전체가 성공해야 commit, 하나라도 실패하면 rollback.
	public int 회원가입(User user) {
		try {
			String rawPassword = user.getPassword();	// 1234 원문
			String encPassword = encoder.encode(rawPassword);		// 해쉬(단방향 암호화)
			user.setPassword(encPassword);
			user.setRole(RoleType.USER);		// 회원가입 시 role은 별도의 입력을 받지 않기에 이런식으로 값을 넣도록 함.
			userRepository.save(user);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("UserService : 회원가입() : " + e.getMessage());
		}
		return -1;
	}

	@Transactional
	public void 회원수정(User user) {
		// 수정 시에는 영속성 컨텍스트에 User 오브젝트를 영속화시키고, 영속화된 User 오브젝트를 수정
		// Select해서 user 오브젝트를 DB로부터 가져오는 이유는 영속화를 하기위해서!
		// 영속화된 오브젝트를 변경하면 자동으로 DB에 Update문을 날려준다!	(더티체킹)
		User persistance =  userRepository.findById(user.getId())
				.orElseThrow(()->{
					return new IllegalArgumentException("회원 찾기 실패");
				});
		
		// Validate 체크
		// 일반 회원가입 사용자만이 password를 변경할 수 있도록 처리 (OAuth 로그인을 통한 자동 회원가입자는 password 변경불가!)
		// 이메일도 변경못하도록 처리
		if (persistance.getOauth() == null || persistance.getOauth().equals("")) {
			String rawPassword = user.getPassword();	// 1234 원문
			String encPassword = encoder.encode(rawPassword);		// 해쉬(단방향 암호화)
			
			persistance.setPassword(encPassword);
			persistance.setEmail(user.getEmail());
		}
		
		// 회원수정 함수 종료 시 = 서비스 종료 = 트랜잭션 종료 = commit이 자동으로 된다.
		// 영속화된 persistance 객체의 변화가 감지되면 더티체킹이 되어 update문을 날려준다.
	}
	
	/* 스프링 시큐리티 로그인을 사용할 것이므로 이 로직은 필요없음. (전통적인 로그인 방식임)
	 * @Transactional(readOnly = true) // Select할 때 트랜잭션 시작, 서비스 종료시에 트랜잭션 종료 (정합성을
	 * 유지할 수 있다.) public User 로그인(User user) { return
	 * userRepository.findByUsernameAndPassword(user.getUsername(),
	 * user.getPassword()); }
	 */
}
