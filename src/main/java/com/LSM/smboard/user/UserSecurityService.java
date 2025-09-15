package com.LSM.smboard.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService implements UserDetailsService {

	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		Optional<SiteUser> _siteUser = userRepository.findByUsername(username);
		if(_siteUser.isEmpty()) {//참이면 해당 아이디 없음
			throw new UsernameNotFoundException("해당 사용자를 찾을수 없습니다");
		} 
		SiteUser siteUser = _siteUser.get(); //아이디로 찾은 레코드
		List<GrantedAuthority> autorities = new ArrayList<>();
		//사용자의 권한 정보를 나타내는 GrantedAuthority 객체들의 리스트
		
		if("admin".equals(username)) { //참이면 admin권한
			autorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
		} else { //일반 user권한
			autorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
		}
		
		return new User(siteUser.getUsername(), siteUser.getPassword(), autorities);
	}
	
	
}
