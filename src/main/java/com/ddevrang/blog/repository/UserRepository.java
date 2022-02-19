package com.ddevrang.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddevrang.blog.model.User;

// DAO (JSP에서 DAO와 같은 역할)
// 자동으로 Bean 등록이 된다.(스프링 IoC에서 객체를 가지고 있는 것)
//@Repository // 이전에는 명시해야했으나 이제는 생략 가능. (자동으로 Bean 등록)
public interface UserRepository extends JpaRepository<User, Integer>{
	
	// JPA Naming 쿼리
	// SELECT * FROM user WHERE username = ?1;
	Optional<User> findByUsername(String username);
	
	// 스프링 시큐리티 로그인을 사용할 것이므로 이 로직은 필요없음. (전통적인 로그인 방식임)
	// JPA Naming 쿼리
	// 실제로 JPA에 findByUsernameAndPassword라는 함수는 없지만, 양식에 맞는 이름으로 만들면 자동으로 쿼리가 생성되고 실행됨
	// SELECT * FROM user WHERE username = ?1 AND password = ?2; 쿼리가 만들어지고 ?에는 파라메터의 값이 순서대로 대입된다.
//	User findByUsernameAndPassword(String username, String password);
	
	// 혹은 아래와 같이 nativeQuery를 직접 만드는 방법도 있다. userRepository의 login을 호출하면 value에 설정한 쿼리가 실행된다.
	// JPA 명령어나 JPA Naming 쿼리 방식으로 구현하기에 복잡한 경우 사용한다.
	// @Query(value = "SELECT * FROM user WHERE username = ?1 AND password = ?2", nativeQuery = true)
	// User login(String username, String password);
	
}
