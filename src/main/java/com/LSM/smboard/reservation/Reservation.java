package com.LSM.smboard.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.LSM.smboard.answer.Answer;
import com.LSM.smboard.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity //DB테이블과 매핑활 entity클래스로 설정
@Table(name = "reservation") //실제로 매핑될 데이터베이스의 테이블 이름 설정
@SequenceGenerator(
		name = "RESERVATION_SEQ_GENERATOR",//JPA 내부 시퀀스 이름
		sequenceName = "RESERVATION_SEQ", //실제 DB 시퀀스 이름
		initialValue = 1, //시퀀스 시작값
		
		
		allocationSize = 1 //시퀀스 증가치
		)
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARDLIST_SEQ_GENERATOR")
	private Integer id; //질문 게시판의 글번호(기본키-자동증가)
	
	@Column(length = 200) //질문 게시판 제목은 200자까지 가능
	private String subject; //질문 게시판의 제목
	
	@Column(length = 500)
	private String content; //질문 게시판의 내용
	
	@CreatedDate
	private LocalDateTime createdate;
	
	//1:N 관계 ->질문:답변들 ->@one To many
	@OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE) 
	//cascade ->질문글(부모글)이 삭제될 경우 답변들(자식글)이 함께 삭제되게 하는 설정
	private List<Answer> answerList;
	
	// N:1관계 -> 작성자 1명이 여러 질문 작성 가능
	@ManyToOne
	private SiteUser author; //글쓴이(1명)
	
	private LocalDateTime modifydate; //질문글 수정 일시
	
	//N:N관계 질문:추천자 
	@ManyToMany
	Set<SiteUser> voter; //추천한 유저가 중복없이 여러명의 유저가 저장, 추천수
	//set-> 중복 제거용 컬랙션
	
	//N:N관계 질문:추천자 
	@ManyToMany
	Set<SiteUser> disvoter; //추천한 유저가 중복없이 여러명의 유저가 저장, 추천수
	//set-> 중복 제거용 컬랙션MultipartFile
	
	
	
	private Integer hit=0; //질문글 조회수
	
	// 날짜 필드 추가
    private LocalDate reserveDate;
    private LocalTime reserveTime;
    
   
    
    private int price; 
    
    private String location; 
    

	
    private Integer completed =0; // 거래 완료 여부, 기본값 false
    
    private String status; // 예약 상태 (예: "대기", "승인", "거절")
    private String contact; // 거래자 연락처
    private String note;    // 예약 시 참고사항
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationRequest> reservations = new ArrayList<>();
}

