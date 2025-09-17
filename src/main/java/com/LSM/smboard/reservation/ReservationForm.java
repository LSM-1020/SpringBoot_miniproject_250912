package com.LSM.smboard.reservation;

import java.time.LocalDate;
import java.time.LocalTime;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationForm {
	
		@NotEmpty(message = "제목은 필수 항목입니다") //공란으로 들어오면 작동
		@Size(max=200,message="제목은 200자 이하로 작성가능합니다") //최대 200글자까지 허용
		@Size(min=5,message="제목은 5자 이상 작성하셔야 합니다") //최소 5글자 이상만 허용
		private String subject;
		
		
		@NotEmpty(message = "내용은 필수 항목입니다") //공란으로 들어오면 작동
		@Size(max=500,message="내용은 500자 이하로 작성가능합니다") //최대 500글자까지 허용
		@Size(min=5,message="내용은 5자 이상 작성하셔야 합니다") //최소 5글자 이상만 허용
		private String content;
		
		// 이미지 업로드용
		private MultipartFile image;
		
		private int price;
		@NotEmpty(message = "장소는 필수 항목입니다") //공란으로 들어오면 작동
		private String location;
		
	    private LocalDate reserveDate;  // 예약 날짜
	    private LocalTime reserveTime;  // 예약 시간
	    
	    private String existingImage;
	}

