package com.ddevrang.blog.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.ddevrang.blog.dto.ResponseDto;

@ControllerAdvice // 어디에서든 Exception이 발생했을 때, 이 파일에 접근 가능하도록 하기위함.
@RestController
public class GlobalExceptionHandler {

	// Exception이 발생하였을 때, 아래의 함수를 실행시킬 예정.
		// IllegalArgumentException이 발생하면 그 에러를 아래 함수로 전달해준다. (주석하고 현재는 Exception.class로 설정)
		// @ExceptionHandler(value = IllegalArgumentException.class)
		// public String handleArgumentException(IllegalArgumentException e) {
	@ExceptionHandler(value = Exception.class)
	public ResponseDto<String> handleArgumentException(Exception e) {
		return new ResponseDto<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}
}
