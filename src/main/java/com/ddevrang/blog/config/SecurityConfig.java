package com.ddevrang.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ddevrang.blog.config.auth.PrincipalDetailService;

// 아래의 세가지 옵션은 스프링 시큐리티에 필요한 세트라고 생각하면 된다. 잘 모르겠으면 그냥 셋 다 쓰면 된다.
@Configuration		// 빈 등록(IoC 관리) => 빈(Bean) 등록 : 스프링 컨테이너에서 객체를 관리할 수 있도록 하는 것.
@EnableWebSecurity		// 시큐리티 필터가 등록이 된다. 관련 설정을 아래에서 한다.
@EnableGlobalMethodSecurity(prePostEnabled = true)		// 특정 주소로 접근을 하면 권한 및 인증을 미리 체크하겠다는 뜻.
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired	// DI
	private PrincipalDetailService principalDetailService;
		
	@Bean 	// DI
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean		// Bean 어노테이션을 붙이면 IoC가 된다. => 리턴값을 스프링이 관리한다.
	public BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}
	
	// 시큐리티가 대신 로그인할 때, Password를 가로채기하는데
	// 해당 Password가 뭘로 해쉬가 되어 회원가입이 되었는지 알아야만
	// 같은 해쉬로 암호화하여 DB에 있는 값과 비교할 수 있음.
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(principalDetailService).passwordEncoder(encodePWD());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()		// csrf 토큰 비활성화 (테스트시 걸어두는게 좋음.)
			.authorizeRequests()
				.antMatchers("/", "/auth/**", "/js/**", "/css/**", "/image/**", "/dummy/**")		// 해당 주소들에 대한 요청은
				.permitAll()																												// 모두에게 접근이 허용
				.anyRequest()																											// 그외 나머지 주소들에 대한 요청은
				.authenticated()																										// 인증이 필요함.
			.and()
				.formLogin()
				.loginPage("/auth/loginForm")		// 인증이 필요한 경우 로그인페이지를 보여줌.
				.loginProcessingUrl("/auth/loginProc")		// 스프링 시큐리티가 해당 주소로 요청된 로그인을 가로채서 대신 로그인 해준다.
				.defaultSuccessUrl("/");									// 정상적으로 로그인된 경우 해당 주소로 이동한다.
	}
}
