package com.LSM.smboard.answer;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;


import com.LSM.smboard.board.Board;
import com.LSM.smboard.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "boardanswer") //실제로 매핑될 데이터베이스의 테이블 이름 설정
@SequenceGenerator(
		name = "BOARDANSWER_SEQ_GENERATOR", //JPA 내부 시퀀스 이름
		sequenceName = "BOARDANSWER_SEQ", //실제 DB 시퀀스 이름 
		initialValue = 1, //시퀀스 시작값
		allocationSize = 1 //시퀀스 증가치
		)
public class Answer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARDANSWER_SEQ_GENERATOR")
	private Integer id; //기본키(자동증가 옵션)
	
	@Column(length = 500)
	private String content; //답변 게시판 내용	
	
	private LocalDateTime createdate; //게시판 답변 등록일시
	
	//N:1 관계 -> 답변들:질문 -> @ManyToOne
	@ManyToOne
	private Board board;
	
	//N:1 관계 -> 답변들:작성자 -> @ManyToOne
	@ManyToOne
	private SiteUser author;
	
	private LocalDateTime modifydate; //질문글 수정 일시

	//N:N관계 답변:추천자 
	@ManyToMany
	Set<SiteUser> voter; //추천한 유저가 중복없이 여러명의 유저가 저장, 추천수
	//set-> 중복 제거용 컬랙션
	
	//N:N관계 질문:추천자 
		@ManyToMany
		Set<SiteUser> disvoter; //추천한 유저가 중복없이 여러명의 유저가 저장, 추천수
		//set-> 중복 제거용 컬랙션
	
}