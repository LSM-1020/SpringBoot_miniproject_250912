package com.LSM.smboard;

import java.security.Principal;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute("username")
	public String addUsernameToModel(Principal principal) {
		if (principal !=null) {
			return principal.getName();
			
		}
		return null;
	}
}
