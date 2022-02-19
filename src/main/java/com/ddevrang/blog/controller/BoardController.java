package com.ddevrang.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ddevrang.blog.service.BoardService;

@Controller
public class BoardController {

// 컨트롤러에서 시큐리티 세션을 어떻게 찾을 수 있을지?
//	@AuthenticationPrincipal PrincipalDetail principal 와 같은 방식으로 확인할 수 있다.
	
	@Autowired
	private BoardService boardService;

	@GetMapping({ "", "/" }) // 아무것도 안붙였을 때, / 붙였을 때
	public String index(Model model, @PageableDefault(size=3, sort="id", direction=Sort.Direction.DESC) Pageable pageable) {

		// model에 데이터를 담으면 return할 때 view까지 해당 데이터를 가져간다.
		model.addAttribute("boards", boardService.글목록(pageable));
		
		// application.yml 에 설정한 prefix와 suffix 옵션이 적용됨
		// prefix: /WEB-INF/views/
		// suffix: .jsp
		// 적용 결과 : /WEB-INF/views/index.jsp
		return "index";		// Controller 어노테이션(RestController 아님)이기에 리턴할때 viewResolver가 작동함.
	}
	
	@GetMapping("/board/{id}")
	public String findById(@PathVariable int id, Model model) {
		model.addAttribute("board", boardService.글상세보기(id));
		return "board/detail";
	}
	
	@GetMapping("/board/{id}/updateForm")
	public String updateForm(@PathVariable int id, Model model) {
		model.addAttribute("board", boardService.글상세보기(id));
		
		return "/board/updateForm";
	}
	
	// USER 권한이 필요
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}
	
}
