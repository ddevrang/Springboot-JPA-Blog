package com.ddevrang.blog.test;

import java.util.List;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ddevrang.blog.model.RoleType;
import com.ddevrang.blog.model.User;
import com.ddevrang.blog.repository.UserRepository;

// html 파일이 아니라 data를 리턴해주는 controller => RestController
@RestController
public class DummyControllerTest {
	
	@Autowired // 의존성 주입(DI)
	private UserRepository userRepository;
	
	@DeleteMapping("/dummy/user/{id}")
	public String delete(@PathVariable int id) {
		
		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			return "삭제에 실패하였습니다. 해당 id는 DB에 존재하지 않습니다.";
		}
		
		return "삭제되었습니다. id : "+id;
	}
	
	// http://localhost:8000/blog/dummy/user/1
	// update 요청이기 때문에 @PutMapping으로 처리
	// 이전에 실습한 select에서 동일한 url을 사용하였으나
	// 그것은 Get 요청이고, 이번엔 Put 요청이라
	// 중복되어도 알아서 구별하므로 동일한 url을 사용해도 된다.
	// email과 password를 수정하도록 할 것.
	@PutMapping("/dummy/user/{id}")
	@Transactional	// save 명령어 대신 사용하여 동일한 update 작업을 할 수 있음. => 더티체킹
								// 함수 종료시에 자동으로 commit이 됨.
	public User updateUser(@PathVariable int id, @RequestBody User requestUser) {
																					// JSON 데이터를 요청 => Java Object로 받아줌. @RequestBody의 기능.
																					// MessageConverter의 Jackson 라이브러리가 변환하여 받아줌. (매우 편리)
		System.out.println("id = "+id);
		System.out.println("password = "+requestUser.getPassword());
		System.out.println("email = "+requestUser.getEmail());
		
		// db에 대응하는 id가 없을 경우 null 방지를 위해 orElseThrow 처리하였고,
		// 람다표현식을 이용하여 파라메터에 바로 함수를 넣어서 처리.
		User user = userRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("수정에 실패하였습니다.");
		});
		
		user.setPassword(requestUser.getPassword());
		user.setEmail(requestUser.getEmail());
		
		// save는 원래 insert할 때 사용하는 명령어인데
		// id를 전달하지 않으면 insert해주고,
		// id를 전달하였을 때 id에 해당하는 값이 이미 db에 존재하면 update해줌.
		// id를 전달하였을 때 id에 해당하는 값이 없다면 insert 해줌.
		// 그러나 수정하려는 값만 가지고 save하면 나머지 다른 값들은 null이 되어 문제가 발생.
		// 따라서 수정하려는 row의 값 전체를 읽어서 객체에 저장하고,
		// 수정하려는 값을 다시 set해준다. 그리고 save하면 모든 값이 존재하므로 정상처리 된다.
//		userRepository.save(user);
		// save 명령을 하지않고, @Transactional 어노테이션을 추가함으로 동일한 update 작업이 가능. 이를 더티체킹이라 함.
		
		// 더티 체킹
		return user;
	}
	
	// user 테이블의 정보를 일괄 select
	// http://localhost:8000/blog/dummy/users
	@GetMapping("/dummy/users")
	public List<User> list(){
		return userRepository.findAll();
	}
	
	// 한 페이지당 2건의 데이터를 리턴받게 페이징 처리
	// http://localhost:8000/blog/dummy/user
	@GetMapping("/dummy/user")
	public List<User> pageList(@PageableDefault(size=2, sort="id", direction=Sort.Direction.DESC) Pageable pageable){
		Page<User> pagingUser = userRepository.findAll(pageable);
		
		List<User> users = pagingUser.getContent();
		
		return users;
	}
		
	// {id} 주소로 파라미터를 전달 받을 수 있음.
	// http://localhost:8000/blog/dummy/user/3
	@GetMapping("/dummy/user/{id}")
	public User detail(@PathVariable int id) {
		// user/4를 찾을 때, 데이터베이스에서 못찾게 되면 
		// user객체는 null이 될 것인데
		// 그럼 최종적으로 return null이 된다 => 프로그램에 문제가 발생
		// 따라서 Optional로 너의 User 객체를 감싸서 가져올테니
		// null인지 아닌지 판단해서 return해라!
		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
			@Override
			public IllegalArgumentException get() {
				return new IllegalArgumentException("해당 사용자는 존재하지 않습니다. id : "+id);
			}
		});
		
//		// 람다식
//		User user = userRepository.findById(id).orElseThrow(()-> {
//			return new IllegalArgumentException("해당 유저는 없습니다(람다). id : "+id);
//	});
		
//		User user = userRepository.findById(id).orElseGet(new Supplier<User>() {
//			@Override
//			public User get() {
//				// TODO Auto-generated method stub
//				return new User();
//			}
//		});
		
		// 요청 : 웹 브라우저
		// 리턴하는 user 객체 = 자바 오브젝트
		// 자바 오브젝트로 리턴했을 때, 웹 브라우저가 이해하지 못함.
		// 따라서 웹 브라우저가 이해할 수 있는 데이터로 변환해야 함. -> JSON
		// 예전 스프링에서는 Gson 라이브러리 같은 것을 사용해서 변환하였으나
		// 스프링부트에서는 MessageConverter가 응답시에 자동으로 작동 함.
		// 만약 자바 오브젝트를 리턴하면 
		// MessageConverter가 Jackson 라이브러리를 호출해서
		// user 객체를 json으로 변환해서 브라우저에게 던져준다.
		// 그렇기에 지금 따로 변환 작업이 없이 사용가능한 것!
		return user;
	}
	
	// http://localhost:8000/blog/dummy/join (요청)
	// http의 body에 username, password, email 데이터를 가지고 요청하면 그 결과가 받아짐.
	@PostMapping("/dummy/join")
//	public String join(@RequestParam("username") String username, String password, String email) { // 변수명을 적기만 하면 받아준다.
//																																														 // key, value로 규칙을 지켜 보내면 스프링이 알아서 파싱해준다.
//		System.out.println("username : "+username);
//		System.out.println("password : "+password);
//		System.out.println("email : "+email);
	
	public String join(User user) { // 위의 주석된 방법보다 더 편리하게 object로 받아서 처리할 수도 있다.
	
		System.out.println("username : "+user.getUsername());
		System.out.println("password : "+user.getPassword());
		System.out.println("email : "+user.getEmail());
		
		user.setRole(RoleType.USER);
		userRepository.save(user);
		return "회원가입이 완료되었습니다.";
	}

}
