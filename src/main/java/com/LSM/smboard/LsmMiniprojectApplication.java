package com.LSM.smboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;



@SpringBootApplication

public class LsmMiniprojectApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		
		return builder.sources(LsmMiniprojectApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(LsmMiniprojectApplication.class, args);
	}
	
	}