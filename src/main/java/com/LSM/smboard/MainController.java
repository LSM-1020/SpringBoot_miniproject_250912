package com.LSM.smboard;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/")
	public String index(Model model, Principal principal) {
	    if (principal != null) {
	        model.addAttribute("username", principal.getName());
	    }
	    return "index";
	}
 
}
