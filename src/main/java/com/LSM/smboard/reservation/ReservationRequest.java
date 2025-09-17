package com.LSM.smboard.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.LSM.smboard.user.SiteUser;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request") //실제로 매핑될 데이터베이스의 테이블 이름 설정
@SequenceGenerator(
		name = "REQUEST_SEQ_GENERATOR",//JPA 내부 시퀀스 이름
		sequenceName = "REQUEST_SEQ", //실제 DB 시퀀스 이름
		initialValue = 1, //시퀀스 시작값
		
		
		allocationSize = 1 //시퀀스 증가치
		)
@EntityListeners(AuditingEntityListener.class)
public class ReservationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    private Reservation reservation;  // 어떤 글에 대한 요청인지

    @ManyToOne
    @JoinColumn(name = "username_id")
    private SiteUser user; //글쓴이(1명)                // 요청한 사용자

    private LocalDate reserveDate;
    private LocalTime reserveTime;

    private LocalDateTime createdate = LocalDateTime.now();

    // getter/setter
}



