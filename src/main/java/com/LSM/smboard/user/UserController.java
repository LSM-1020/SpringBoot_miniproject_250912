package com.LSM.smboard.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping(value="/login")
	public String login() {
		
		return "login";
	}
	@GetMapping(value="/signup")
	public String signup() {
		
		return "signup";
	}
	@PostMapping(value="/signup") //회원가입 정보를 DB에 입력
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "signup";
		}
		//비밀번호 확인 실패
		if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
			bindingResult.rejectValue("password2","passwordInCorrect","비밀번호 확인이 일치하지 않습니다.");
			return "signup";
		}
		try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(),	userCreateForm.getPassword1());
			
		} catch(DataIntegrityViolationException e) { //중복된 데이터에 대한 예외처리
			e.printStackTrace();
			//이미 등록된 사용자 아이디의 경우 발생하는 에러 추가
			bindingResult.reject("signupFailed","이미등록된 사용자 입니다");
			return "signup";
		} catch (Exception e) { //기타 예외처리
			e.printStackTrace();
			bindingResult.reject("signupFailed","회원가입 실패");
			return "signup";
		}
			return "login";//첫 화면으로 이동
		}

}
