package com.LSM.smboard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration //스프링부트의 환경설정 파일이라는 것을 명시하는 annotation
@EnableWebSecurity //모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 annotation
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	   SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	      http
	         .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
	               .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
	         		.requestMatchers(new AntPathRequestMatcher("/error")).permitAll())
	         
	         .formLogin((formLogin) -> formLogin //스프링 시큐리티에서 로그인 설정
	        		 .loginPage("/user/login") //로그인 요청
	        		 .defaultSuccessUrl("/")) //로그인 성공시 이동할 페이지 루트로 지정
	         
	         .logout((logout) -> logout
	        		 .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) //로그아웃 요청      		 
	        		 .logoutSuccessUrl("/?logout")//로그아웃 성공시 이동할 페이지 지정
	        		 .invalidateHttpSession(true)) //세션 삭제
	        		
	         // CSRF 비활성화 (모든 요청 허용용)
	            .csrf(csrf -> csrf.disable())
	         ;
	      return http.build();
	   }
		
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean //스프링 시큐리티에서 인증을 처리하는 매니저 클래스
	AuthenticationManager authenticationManager(AuthenticationConfiguration
			authenticationConfiguration) throws Exception{
	return authenticationConfiguration.getAuthenticationManager();
	}
	
				
}
