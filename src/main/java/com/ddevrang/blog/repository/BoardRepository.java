package com.ddevrang.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddevrang.blog.model.Board;

// DAO (JSP에서 DAO와 같은 역할)
// 자동으로 Bean 등록이 된다.(스프링 IoC에서 객체를 가지고 있는 것)
//@Repository // 이전에는 명시해야했으나 이제는 생략 가능. (자동으로 Bean 등록)
public interface BoardRepository extends JpaRepository<Board, Integer>{
	
}
