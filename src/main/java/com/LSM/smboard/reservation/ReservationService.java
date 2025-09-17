package com.LSM.smboard.reservation;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import com.LSM.smboard.DataNotFoundException;
import com.LSM.smboard.answer.Answer;
import com.LSM.smboard.user.SiteUser;
import com.LSM.smboard.user.UserRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	
	private final ReservationRequestRepository reservationRequestRepository;
	
	private final UserRepository userRepository;
	
	
	//@requiredArgsconstructor에 의해 생성자 방식으로 주입된 questionRepository (final필드만 가능)
	
	public List<Reservation> getlist() {//모든 질문글 가져오기->페이징
		return this.reservationRepository.findAll();
	}
	public void addRequest(Integer reservationId, String date, String time, String username) {
		Reservation reservation = reservationRepository.findById(reservationId)
		        .orElseThrow(() -> new IllegalArgumentException("예약 글이 존재하지 않습니다."));

        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        ReservationRequest request = new ReservationRequest();
        request.setReservation(reservation);
        request.setUser(user);
        request.setReserveDate(LocalDate.parse(date));
        request.setReserveTime(LocalTime.parse(time));

        reservationRequestRepository.save(request);
    }
	public Reservation getBoard(Integer id) {
		Optional<Reservation> bOptional = reservationRepository.findById(id);
		if(bOptional.isPresent()) {
			return bOptional.get(); //question반환
		} else {
			throw new DataNotFoundException("question not found");
		}
	}
	
	public Reservation create(String subject, String content, SiteUser author, LocalDate reserveDate, LocalTime reserveTime,
            int price, String location) {

Reservation reservation = new Reservation();
reservation.setSubject(subject);
reservation.setContent(content);
reservation.setAuthor(author);
reservation.setCreatedate(LocalDateTime.now()); // 생성일
reservation.setReserveDate(reserveDate);
reservation.setReserveTime(reserveTime);

reservation.setPrice(price);          // 추가
reservation.setLocation(location);    // 추가
return reservationRepository.save(reservation);
    }

	
	// 페이징 + 검색 (field 구분)
	public Page<Reservation> getPageBoards(int page, String field, String kw) {
	    int size = 10;
	    int startRow = page * size;
	    int endRow = startRow + size;

	    List<Reservation> reservateion;
	    long total;

	    if (kw == null || kw.isEmpty()) {
	    	reservateion = reservationRepository.findBoardsWithPaging(startRow, endRow);
	        total = reservationRepository.countAllBoards();
	    } else {
	        switch(field) {
	            case "subject":
	            	reservateion = reservationRepository.searchBoardsBySubjectWithPaging(kw, startRow, endRow);
	                total = reservationRepository.countSearchBySubject(kw);
	                break;
	            case "content":
	            	reservateion = reservationRepository.searchBoardsByContentWithPaging(kw, startRow, endRow);
	                total = reservationRepository.countSearchByContent(kw);
	                break;
	            case "author":
	            	reservateion = reservationRepository.searchBoardsByAuthorWithPaging(kw, startRow, endRow);
	                total = reservationRepository.countSearchByAuthor(kw);
	                break;
	            default:
	            	reservateion = reservationRepository.searchBoardsWithPaging(kw, startRow, endRow);
	                total = reservationRepository.countSearchResult(kw);
	        }
	    }

	    // page가 totalPages를 넘어갈 경우 마지막 페이지로 보정
	    int totalPages = (int) Math.ceil((double) total / size);
	    if(page >= totalPages && totalPages > 0) {
	        page = totalPages - 1;
	        startRow = page * size;
	        endRow = startRow + size;
	        // 재조회
	        if (kw == null || kw.isEmpty()) {
	        	reservateion = reservationRepository.findBoardsWithPaging(startRow, endRow);
	        } else {
	            switch(field) {
	                case "subject":
	                	reservateion = reservationRepository.searchBoardsBySubjectWithPaging(kw, startRow, endRow);
	                    break;
	                case "content":
	                	reservateion = reservationRepository.searchBoardsByContentWithPaging(kw, startRow, endRow);
	                    break;
	                case "author":
	                	reservateion = reservationRepository.searchBoardsByAuthorWithPaging(kw, startRow, endRow);
	                    break;
	                default:
	                	reservateion = reservationRepository.searchBoardsWithPaging(kw, startRow, endRow);
	            }
	        }
	    }

	    return new PageImpl<>(reservateion, PageRequest.of(page, size), total);
	}

	public void modify(Reservation reservation, String subject, String content,
			            LocalDate reserveDate, LocalTime reserveTime,
			            Integer price, String location) {
			reservation.setSubject(subject);
			reservation.setContent(content);
			reservation.setReserveDate(reserveDate);
			reservation.setReserveTime(reserveTime);
			
			reservation.setPrice(price);
			reservation.setLocation(location);
			reservation.setModifydate(LocalDateTime.now());
			
			// 기존 글을 업데이트
			reservationRepository.save(reservation);
			}
	public void delete(Reservation reservation) {
		reservationRepository.delete(reservation);
	}
	
	
	
	public void vote(Reservation reservation, SiteUser siteUser) { //업데이트문으로 만들어줘야함
		reservation.getVoter().add(siteUser);
		//question은 추천을 받은 글의 번호로 조회한 엔티티
		//question의 멤버인 voter를 get해서 voter에 추천을 누른 유저의 엔티티를 추가
		reservationRepository.save(reservation); //추천한 유저수가 변경된 질문 엔티티를 다시 save해서 갱신
	}
	
	public void disvote(Reservation reservation, SiteUser siteUser) { //업데이트문으로 만들어줘야함
		reservation.getDisvoter().add(siteUser);
		//question은 추천을 받은 글의 번호로 조회한 엔티티
		//question의 멤버인 voter를 get해서 voter에 추천을 누른 유저의 엔티티를 추가
		reservationRepository.save(reservation); //추천한 유저수가 변경된 질문 엔티티를 다시 save해서 갱신
	}
	
	public void hit(Reservation reservation) { //조회수 증가questionRepository.updateHit(id);
		reservation.setHit(reservation.getHit()+1);
		reservationRepository.save(reservation);
	}
	
	private Specification<Reservation> search(String kw) { //키워드(kw) 검색조회 
		
		return new Specification<Reservation>() {
			private static final long SerialVersionUID = 1L;
			
			@Override
			public Predicate toPredicate(Root<Reservation> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true); //distinct->중복 제거
				Join<Reservation, SiteUser> u1 = q.join("author",JoinType.LEFT); //question+siteUser테이블과 left조인
				Join<Reservation, Answer> a = q.join("answerList",JoinType.LEFT); //question+answer테이블과 조인
				Join<Reservation, SiteUser> u2 = a.join("author",JoinType.LEFT);	//answer+siteUser테이블과 left조인
				
				return cb.or(cb.like(q.get("subject"), "%"+kw+"%"), //질문 제목에서 검색어 조회
						cb.like(q.get("content"), "%"+kw+"%"),//질문 내용에서 검색어 조회
						cb.like(u1.get("username"), "%"+kw+"%"),//질문 작성자에서 검색어 조회
						cb.like(a.get("content"), "%"+kw+"%"),//답변 내용에서 검색어 조회
						cb.like(u2.get("username"), "%"+kw+"%")//답변 작성자에서 검색어 조회
						) 
					
						;
			}
		};
		
		
	}
	
}
