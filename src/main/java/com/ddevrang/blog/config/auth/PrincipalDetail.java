package com.ddevrang.blog.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ddevrang.blog.model.User;

import lombok.Getter;

// 스프링 시큐리티가 로그인 요청을 가로채서 로그인을 진행하고 완료가 되면 UserDetails 타입의 오브젝트를 
// 스프링 시큐리티의 고유한 세션 저장소에 저장을 해준다.
@Getter
public class PrincipalDetail implements UserDetails {
	private User user; // 컴포지션 (객체를 품고 있는 것)

	public PrincipalDetail(User user) {
		this.user = user;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	// 계정만료 여부
	// 계정이 만료되지 않았는지를 리턴한다. (true : 계정만료 안됨, false : 계정만료)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정잠금 여부
	// 계정이 잠금되지 않았는지를 리턴한다. (true : 계정잠금 안됨, false : 계정잠금)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 비밀번호만료 여부
	// 비밀번호가 만료되지 않았는지를 리턴한다. (true : 비밀번호만료 안됨, false : 비밀번호만료)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정활성화(사용가능) 여부
	// 계정활성화가 되었는지를 리턴한다. (true : 계정활성화, false : 계정활성화 안됨)
	@Override
	public boolean isEnabled() {
		return true;
	}

	// 계정이 가진 권한 목록을 리턴한다. (계정이 여러개 있을 수 있어서 루프를 돌려야 하는데 우리는 권한이 한개이므로 생략)
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collectors = new ArrayList<>();

//		collectors.add(new GrantedAuthority() {
//			
//			@Override
//			public String getAuthority() {
//				return "ROLE_"+user.getRole();	// 스프링에서 Role을 리턴하는 형식  ex) ROLE_USER
//			}
//		});
		// 위에 주석한 내용을 람다표현식으로 간략하게 표현할 수 있음.
		collectors.add(() -> {
			return "ROLE_" + user.getRole();
		}); // 스프링에서 Role을 리턴하는 형식 ex) ROLE_USER

		return collectors;
	}

}
