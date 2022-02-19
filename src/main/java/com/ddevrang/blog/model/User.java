package com.ddevrang.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더패턴!
//ORM ->  JAVA(다른언어도 포함)의 Object를 Database의 테이블로 매핑해주는 기술
@Entity // User 클래스가 MySQL에 테이블이 자동생성된다.
//@DynamicInsert // Insert할때 null 인 필드는 제외하고 Insert 한다. ==> 다만 남용하면 좋지않음. 어노테이션을 쓸게 너무 많아진다.
public class User {

	@Id // Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.
	private int id; // 시퀀스(오라클), auto_increment(mysql) ==> 설정한 DB에 따라 달라짐

	@Column(nullable = false, length = 100, unique = true) // unique 옵션을 주면 해당 컬럼의 값은 중복 불가.
	private String username; // 아이디

	@Column(nullable = false, length = 100) // 비밀번호를 암호화(해쉬) 할 것이므로 length를 길게 잡음
	private String password;

	@Column(nullable = false, length = 50)
	private String email;

	// @ColumnDefault("'user'") // " ' user' " 문자라는 것을 알려주기 위해 ' ' 으로 감싼다. ==>
	// DynamicInsert 처리 하지않기위해 주석처리 (기본값 제거)
	@Enumerated(EnumType.STRING) // DB는 RoleType이라는게 없으므로 해당 ENUM이 String임을 알려준다.
	private RoleType role; // Enum을 쓰는게 좋다. (특정 값만 들어갈 수 있도록 도메인을 설정할 수 있다.)// admin, user, manager
							// 도메인이 정해진다는 것은 범위를 지정한다는 의미. ex) 성별 : 남, 여
							// Enum을 쓰지 않는 경우 상황에따라 엉뚱한 값이 들어갈 수 있음.

	private String oauth;		// 일반 회원가입인 경우 null이고, oauth 로그인한 경우만 정보가 입력됨 ex) kakao, google
	
	// 내가 직접 시간을 넣으려면 Timestamp.valueOf(LocalDateTime.now())
	@CreationTimestamp // 시간이 자동으로 입력
	private Timestamp createDate;
}
