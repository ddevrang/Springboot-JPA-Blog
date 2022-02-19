package com.ddevrang.blog.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더패턴!
@Entity
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
	private int id;
	
	@Column(nullable = false, length = 100)
	private String title;
	
	@Lob // 대용량 데이터
	private String content; // 섬머노트 라이브러리 <html>태그가 섞여서 디자인이 됨. (내용이 길어진다)
	
	private int count; // 조회수
	
	@ManyToOne(fetch = FetchType.EAGER) // Many = Board, User = One, ManyToOne인 경우 기본 fetch전략이 EAGER가 된다. (1건만 있으므로)
	@JoinColumn(name = "userId")
	private User user; // DB는 오브젝트를 저장할 수 없다. (그래서 FK를 사용) 그러나 자바는 Object를 저장할 수 있다.
	
	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) // One = Board, Many = Reply, OneToMany인 경우 기본 fetch전략이 LAZY가 된다.(필요한 경우만 가져옴)
	                                                                      // mappedBy : 연관관계의 주인이 아니다. (FK가 아님) => DB에 컬럼을 만들지 않는다. (join하여 결과가 필요할 뿐)
	@JsonIgnoreProperties({"board"})		// board와 reply간 무한참조를 방지하는 방법.
	@OrderBy("id desc")
	private List<Reply> replys;
	
	@CreationTimestamp // 데이터가 insert나 update 될 때 시간이 자동으로 입력
	private Timestamp createDate;
}
