package com.ddevrang.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ddevrang.blog.model.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer>{
	
	@Modifying		// 이 어노테이션을 안붙였을때는 Select 쿼리로 착각하여 자꾸 ResultSet을 찾는 에러가 발생함. + return 타입을 int로 지정함
	// mSave는 네이밍쿼리가 아니기에 직접 쿼리를 설정해주어야 함
	@Query(value = "INSERT INTO reply(userId, boardId, content, createDate) VALUES(?1, ?2, ?3, now())", nativeQuery = true)
	int mSave(int userId, int boardId, String content);		// 업데이트 된 행의 갯수를 리턴해준다. (에러 발생 시 -1을 리턴함)
	
}
