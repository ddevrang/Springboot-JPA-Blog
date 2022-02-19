package com.ddevrang.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddevrang.blog.dto.ReplySaveRequestDto;
import com.ddevrang.blog.model.Board;
import com.ddevrang.blog.model.User;
import com.ddevrang.blog.repository.BoardRepository;
import com.ddevrang.blog.repository.ReplyRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌. (IoC를 해준다 => 메모리에 띄워준다.)
@Service
public class BoardService {

	@Autowired // DI
	private BoardRepository boardRepository;

	@Autowired
	private ReplyRepository replyRepository;

	@Transactional // 아래의 회원가입 과정 전체를 하나의 트랜잭션으로 묶음. 전체가 성공해야 commit, 하나라도 실패하면 rollback.
	public void 글쓰기(Board board, User user) { // title, content를 받음.
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}

	@Transactional(readOnly = true)
	public Page<Board> 글목록(Pageable pageable) {
		return boardRepository.findAll(pageable); // findAll()은 JpaRepository가 기본적으로 가지고 있음.
	}

	@Transactional(readOnly = true)
	public Board 글상세보기(int id) {
		return boardRepository.findById(id).orElseThrow(() -> {
			return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
		});
	}

	@Transactional
	public void 글수정하기(int id, Board requestBoard) {
		Board board = boardRepository.findById(id) // 영속화시킴
				.orElseThrow(() -> {
					return new IllegalArgumentException("글 찾기 실패 : 아이디를 찾을 수 없습니다.");
				});

		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());

		// 해당 함수 종료 시(Service가 종료될 때) 트랜잭션도 종료되는데, 이 때 더티체킹을 하면서 자동업데이트가 된다.(DB로 flush :
		// 커밋이 된다.)
		// 따라서 영속화된 board에 수정하고자하는 값만 set해주면 되고, 별도의 다른 명령은 필요없다.
	}

	@Transactional
	public void 글삭제하기(int id) {
		boardRepository.deleteById(id);
	}

	@Transactional
	public void 댓글쓰기(ReplySaveRequestDto replySaveRequestDto) {

		// 내가 직접 만든 mSave함수(네이티브쿼리)를 사용하면, 아래의 모든 내용(영속화)를 생략할 수 있다.
		replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(),
				replySaveRequestDto.getContent());

//		User user = userRepository.findById(replySaveRequestDto.getUserId())		// 영속화
//				.orElseThrow(()->{
//					return new IllegalArgumentException("댓글 쓰기 실패 : 유저 아이디를 찾을 수 없습니다.");
//				});
//		
//		Board board = boardRepository.findById(replySaveRequestDto.getBoardId())		// 영속화
//				.orElseThrow(()->{
//					return new IllegalArgumentException("댓글 쓰기 실패 : 게시글 아이디를 찾을 수 없습니다.");
//				});

//		Reply reply = Reply.builder()
//				.user(user)
//				.board(board)
//				.content(replySaveRequestDto.getContent())
//				.build();
		// 위의 방법대신 Reply.java에 update 함수를 만들어서 해결하는 것도 가능함.
		// Reply reply = new Reply();
		// reply.update(user, board, replySaveRequestDto.getContent());

//		replyRepository.save(reply);
	}
	
	@Transactional
	public void 댓글삭제(int replyId) {
		replyRepository.deleteById(replyId);
	}

}
