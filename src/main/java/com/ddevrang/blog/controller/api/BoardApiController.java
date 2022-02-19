package com.ddevrang.blog.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ddevrang.blog.config.auth.PrincipalDetail;
import com.ddevrang.blog.dto.ReplySaveRequestDto;
import com.ddevrang.blog.dto.ResponseDto;
import com.ddevrang.blog.model.Board;
import com.ddevrang.blog.service.BoardService;

@RestController
public class BoardApiController {

	@Autowired
	private BoardService boardService;
	
	@PostMapping("/api/board")
	public ResponseDto<Integer> save(@RequestBody Board board, @AuthenticationPrincipal PrincipalDetail principal) {

		boardService.글쓰기(board, principal.getUser());
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);		// 1을 리턴하면 정상이라는 의미
	}
	
	@PutMapping("/api/board/{id}")
	public ResponseDto<Integer> update(@PathVariable int id, @RequestBody Board board) {

		boardService.글수정하기(id, board);
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);		// 1을 리턴하면 정상이라는 의미
	}
	
	@DeleteMapping("/api/board/{id}")
	public ResponseDto<Integer> deleteById(@PathVariable int id){
		
		boardService.글삭제하기(id);
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);		// 1을 리턴하면 정상이라는 의미
	}
	
	@PostMapping("/api/board/{boardId}/reply")
	// 데이터를 받을 때 컨트롤러에서 dto를 만들어서 받는게 좋다.
	// 규모가 큰 상용화된 프로젝트의 경우 데이터(필드)가 굉장히 많아지기에 모델을 오브젝트를 받아서 처리하려면 굉장히 복잡해진다. 
	// public ResponseDto<Integer> replySave(@PathVariable int boardId, @RequestBody Reply reply, @AuthenticationPrincipal PrincipalDetail principal) {
    //	boardService.댓글쓰기(principal.getUser(), boardId, reply);
	// 그래서 dto를 사용한 방식으로 변경하였음.
		public ResponseDto<Integer> replySave(@RequestBody ReplySaveRequestDto replySaveRequestDto) {
		boardService.댓글쓰기(replySaveRequestDto);
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);		// 1을 리턴하면 정상이라는 의미
	}
	
	@DeleteMapping("/api/board/{boardId}/reply/{replyId}")
	public ResponseDto<Integer> replyDetele(@PathVariable int replyId) {
		boardService.댓글삭제(replyId);
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);		// 1을 리턴하면 정상이라는 의미
	}
	
}
