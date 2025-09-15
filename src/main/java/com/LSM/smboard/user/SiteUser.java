package com.LSM.smboard.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userinfo") //실제로 매핑될 데이터베이스의 테이블 이름 설정
@SequenceGenerator(
		name = "INFO_SEQ_GENERATOR", //JPA 내부 시퀀스 이름
		sequenceName = "INFO_SEQ", //실제 DB 시퀀스 이름 
		initialValue = 1, //시퀀스 시작값
		allocationSize = 1 //시퀀스 증가치
		)
public class SiteUser {	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INFO_SEQ_GENERATOR")	
	private Long id; //유저번호, 기본키
	
	@Column(name="username",unique= true) //아이디는 중복 불가
	private String username; //유저 아이디
	
	private String password;
	@Column(name="email",unique= true) 
	private String email;
}
