package com.LSM.smboard.reservation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.LSM.smboard.answer.AnswerForm;
import com.LSM.smboard.answer.AnswerService;
import com.LSM.smboard.user.SiteUser;
import com.LSM.smboard.user.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

		@Autowired
		private AnswerService answerService;
	
		@Autowired
		private ReservationService reservationService;
		
		@Autowired
		private UserService userService;
		
		@Autowired
		private ReservationRepository reservationRepository;
		
		@Autowired
		private ReservationRequestRepository reservationRequestRepository;
		

	    @GetMapping(value = "/detail/{id}") //파라미터이름 없이 값만 넘어왔을때 처리
		public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
	    	reservationService.hit(reservationService.getBoard(id));//조회수 증가
			
			//service에 3을 넣어서 호출
			Reservation reservation = reservationService.getBoard(id);
			model.addAttribute("reservation", reservation);
			return "reservation_detail"; //타임리프 html의 이름
		}
	    
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value = "/create") //질문 등록 폼만 매핑해주는 메서드->GET
		public String boardCreate(ReservationForm reservationForm, Model model) {
			model.addAttribute("reservation",new Reservation());
			return "reservation_form"; //질문 등록하는 폼 페이지 이름
		}
		
		@PreAuthorize("isAuthenticated()")
		@PostMapping("/create")
	    public String createReservation(@ModelAttribute ReservationForm reservationForm, Principal principal, Model model) {

	        SiteUser author = userService.getUser(principal.getName());

	     // 이미지 파일명만 저장 (서버에 실제 저장하지 않음)
	        String imagePath = null;
	        MultipartFile imageFile = reservationForm.getImage();
	        if (imageFile != null && !imageFile.isEmpty()) {
	            String originalFilename = imageFile.getOriginalFilename();
	            String uuid = UUID.randomUUID().toString();
	            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	            String savedFileName = uuid + extension;

	            // DB에 저장할 경로만 생성
	            imagePath = "/upload/" + savedFileName;
	        }

	        reservationService.create(
	                reservationForm.getSubject(),
	                reservationForm.getContent(),
	                author,
	                reservationForm.getReserveDate(),
	                reservationForm.getReserveTime(),
	              
	                reservationForm.getPrice(),
	                reservationForm.getLocation()
	        );

	        return "redirect:/reservation/list";
	    }
		@GetMapping("/list")
		public String list(@RequestParam(value="page", defaultValue="0") int page,
		                   @RequestParam(value="field", required=false) String field,
		                   @RequestParam(value="kw", required=false) String kw,
		                   Model model) {

		    Page<Reservation> boardPage = reservationService.getPageBoards(page, field, kw);

		    int totalPages = boardPage.getTotalPages();
		    if (totalPages == 0) totalPages = 1; // 결과가 없을 경우 1로 보정

		    int currentPage = boardPage.getNumber(); // 0-based
		    int pageGroupSize = 10; // 한 그룹에 표시할 페이지 개수
		    int startPage = (currentPage / pageGroupSize) * pageGroupSize;
		    int endPage = Math.min(startPage + pageGroupSize - 1, totalPages - 1);

		    int firstPage = 0;
		    int lastPage = totalPages - 1;
		    int prevGroupPage = startPage - 1 < 0 ? 0 : startPage - 1;
		    int nextGroupPage = endPage + 1 >= lastPage ? lastPage : endPage + 1;

		    model.addAttribute("paging", boardPage);
		    model.addAttribute("startPage", startPage);
		    model.addAttribute("endPage", endPage);
		    model.addAttribute("firstPage", firstPage);
		    model.addAttribute("lastPage", lastPage);
		    model.addAttribute("prevGroupPage", prevGroupPage);
		    model.addAttribute("nextGroupPage", nextGroupPage);
		    model.addAttribute("field", field);
		    model.addAttribute("kw", kw);

		    return "reservation_list";
		}
		
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/modify/{id}")
		public String boardModify(ReservationForm reservationForm, @PathVariable("id") Integer id, Principal principal, Model model) {
			Reservation reservation = reservationService.getBoard(id);//아이디에 해당하는 엔티티 반환->수정하려는 글의 엔티티
			//글쓴 유저와 로그인한 유저의 동일 여부 재확인
			if(!reservation.getAuthor().getUsername().equals(principal.getName())) { //참이면 수정권한 없음
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다");
			}
			// ReservationForm에 기존 데이터 세팅
		    ReservationForm form = new ReservationForm();
		    form.setSubject(reservation.getSubject());
		    form.setContent(reservation.getContent());
		    form.setReserveDate(reservation.getReserveDate());
		    form.setReserveTime(reservation.getReserveTime());
		    form.setPrice(reservation.getPrice());
		    form.setLocation(reservation.getLocation());
		    // 이미지 단일 처리
		 
			model.addAttribute("reservation",reservation);
			model.addAttribute("reservationForm", form);
			model.addAttribute("reservationId", id);
			return "reservation_form";
		}
		
		
		@PreAuthorize("isAuthenticated()")
		@PostMapping("/modify/{id}")
		public String modifyReservation(@PathVariable("id") Integer id,
		                                @Valid ReservationForm reservationForm,
		                                BindingResult bindingResult,
		                                Principal principal) {
		    if (bindingResult.hasErrors()) {
		        return "reservation_form";
		    }

		    Reservation reservation = reservationService.getBoard(id);

		    if (!reservation.getAuthor().getUsername().equals(principal.getName())) {
		        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		    }

		 
		   

		    reservationService.modify(
		            reservation,
		            reservationForm.getSubject(),
		            reservationForm.getContent(),
		            reservationForm.getReserveDate(),
		            reservationForm.getReserveTime(),
		            
		            reservationForm.getPrice(),
		            reservationForm.getLocation()
		    );

		    return "redirect:/reservation/detail/" + id;
		}
		
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/delete/{id}")
		public String delete(Principal principal,@PathVariable("id") Integer id) {
			
			Reservation reservation = reservationService.getBoard(id);
			//글쓴 유저와 로그인한 유저의 동일 여부 재확인
					if(!reservation.getAuthor().getUsername().equals(principal.getName())) { //참이면 수정권한 없음
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다");
					}
					reservationService.delete(reservation);
					return "redirect:/reservation/list";
		}
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/vote/{id}")
		public String boardVote(Principal principal,@PathVariable("id") Integer id) {
			Reservation reservation = reservationService.getBoard(id);
			SiteUser siteUser = userService.getUser(principal.getName());
			
			reservationService.vote(reservation, siteUser);
			
					return String.format("redirect:/reservation/detail/%s", id);
		}
		
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/disvote/{id}")
		public String boarddisVote(Principal principal,@PathVariable("id") Integer id) {
			Reservation reservation = reservationService.getBoard(id);
			SiteUser siteUser = userService.getUser(principal.getName());
			
			reservationService.disvote(reservation, siteUser);
			
					return String.format("redirect:/reservation/detail/%s", id);
		}
		
//		@PreAuthorize("isAuthenticated()")
//		@PostMapping("/request/{id}")
//		@ResponseBody 
//		public ResponseEntity<?> requestReservation(
//		        @PathVariable("id") Integer id,
//		        @RequestBody Map<String, String> payload,
//		        @AuthenticationPrincipal UserDetails userDetails) {
//
//		    String date = payload.get("reserveDate");
//		    String time = payload.get("reserveTime");
//
//		    // 여기서 예약 서비스 호출
//		    reservationService.addRequest(id, date, time, userDetails.getUsername());
//
//		    return ResponseEntity.ok().build();
//		}
		
		@PreAuthorize("isAuthenticated()")
		@PostMapping("/request/{id}")
		@ResponseBody 
		public ResponseEntity<?> requestReservation(
		        @PathVariable("id") Integer id,
		        @RequestBody Map<String, String> payload,
		        @AuthenticationPrincipal UserDetails userDetails) {

		    String dateStr = payload.get("reserveDate");
		    String timeStr = payload.get("reserveTime");

		    // LocalDateTime 변환
		    LocalDate reserveDate = LocalDate.parse(dateStr);
		    LocalTime reserveTime = LocalTime.parse(timeStr);
		    LocalDateTime reserveDateTime = LocalDateTime.of(reserveDate, reserveTime);

		    // 현재 시간
		    LocalDateTime now = LocalDateTime.now();

		    // 과거 시간 예약 막기
		    if (reserveDateTime.isBefore(now)) {
		        return ResponseEntity.badRequest().body("지난 시간에는 예약할 수 없습니다.");
		    }

		    // 예약 서비스 호출
		    reservationService.addRequest(id, dateStr, timeStr, userDetails.getUsername());

		    return ResponseEntity.ok().build();
		}
		
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/request/delete/{id}")
		public String requestdelete(Principal principal,@PathVariable("id") Integer id) {
			
			ReservationRequest request = reservationRequestRepository.findById(id).orElse(null);
			//글쓴 유저와 로그인한 유저의 동일 여부 재확인
					if(!request.getUser().getUsername().equals(principal.getName())) { //참이면 수정권한 없음
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다");
					}
					Integer reservationId = request.getReservation().getId();
					reservationRequestRepository.delete(request);
					return "redirect:/reservation/detail/" + reservationId;
		}
		
	}
