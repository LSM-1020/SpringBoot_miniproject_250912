package com.LSM.smboard.board;

import java.time.LocalDateTime;
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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	
	private final BoardRepository boardRepository;
	//@requiredArgsconstructor에 의해 생성자 방식으로 주입된 questionRepository (final필드만 가능)
	
	public List<Board> getlist() {//모든 질문글 가져오기->페이징
		return this.boardRepository.findAll();
	}
	
	public Board getBoard(Integer id) {
		Optional<Board> bOptional = boardRepository.findById(id);
		if(bOptional.isPresent()) {
			return bOptional.get(); //question반환
		} else {
			throw new DataNotFoundException("question not found");
		}
	}
	
	public void create(String subject, String content, SiteUser author) {
		Board board = new Board();
		board.setSubject(subject);
		board.setContent(content);
		board.setCreatedate(LocalDateTime.now());
		board.setAuthor(author);
		boardRepository.save(board);
		
	}
	
	// 페이징 + 검색 (field 구분)
	public Page<Board> getPageBoards(int page, String field, String kw) {
	    int size = 10;
	    int startRow = page * size;
	    int endRow = startRow + size;

	    List<Board> boardList;
	    long total;

	    if (kw == null || kw.isEmpty()) {
	        boardList = boardRepository.findBoardsWithPaging(startRow, endRow);
	        total = boardRepository.countAllBoards();
	    } else {
	        switch(field) {
	            case "subject":
	                boardList = boardRepository.searchBoardsBySubjectWithPaging(kw, startRow, endRow);
	                total = boardRepository.countSearchBySubject(kw);
	                break;
	            case "content":
	                boardList = boardRepository.searchBoardsByContentWithPaging(kw, startRow, endRow);
	                total = boardRepository.countSearchByContent(kw);
	                break;
	            case "author":
	                boardList = boardRepository.searchBoardsByAuthorWithPaging(kw, startRow, endRow);
	                total = boardRepository.countSearchByAuthor(kw);
	                break;
	            default:
	                boardList = boardRepository.searchBoardsWithPaging(kw, startRow, endRow);
	                total = boardRepository.countSearchResult(kw);
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
	            boardList = boardRepository.findBoardsWithPaging(startRow, endRow);
	        } else {
	            switch(field) {
	                case "subject":
	                    boardList = boardRepository.searchBoardsBySubjectWithPaging(kw, startRow, endRow);
	                    break;
	                case "content":
	                    boardList = boardRepository.searchBoardsByContentWithPaging(kw, startRow, endRow);
	                    break;
	                case "author":
	                    boardList = boardRepository.searchBoardsByAuthorWithPaging(kw, startRow, endRow);
	                    break;
	                default:
	                    boardList = boardRepository.searchBoardsWithPaging(kw, startRow, endRow);
	            }
	        }
	    }

	    return new PageImpl<>(boardList, PageRequest.of(page, size), total);
	}

	public void modify(Board board,String subject, String content) {
		
		board.setContent(content);
		board.setSubject(subject);
		board.setModifydate(LocalDateTime.now());
		boardRepository.save(board);
	}
	public void delete(Board board) {
		boardRepository.delete(board);
	}
	
	public void vote(Board board, SiteUser siteUser) { //업데이트문으로 만들어줘야함
		board.getVoter().add(siteUser);
		//question은 추천을 받은 글의 번호로 조회한 엔티티
		//question의 멤버인 voter를 get해서 voter에 추천을 누른 유저의 엔티티를 추가
		boardRepository.save(board); //추천한 유저수가 변경된 질문 엔티티를 다시 save해서 갱신
	}
	
	public void disvote(Board board, SiteUser siteUser) { //업데이트문으로 만들어줘야함
		board.getDisvoter().add(siteUser);
		//question은 추천을 받은 글의 번호로 조회한 엔티티
		//question의 멤버인 voter를 get해서 voter에 추천을 누른 유저의 엔티티를 추가
		boardRepository.save(board); //추천한 유저수가 변경된 질문 엔티티를 다시 save해서 갱신
	}
	
	public void hit(Board board) { //조회수 증가questionRepository.updateHit(id);
		board.setHit(board.getHit()+1);
		boardRepository.save(board);
	}
	
	private Specification<Board> search(String kw) { //키워드(kw) 검색조회 
		
		return new Specification<Board>() {
			private static final long SerialVersionUID = 1L;
			
			@Override
			public Predicate toPredicate(Root<Board> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true); //distinct->중복 제거
				Join<Board, SiteUser> u1 = q.join("author",JoinType.LEFT); //question+siteUser테이블과 left조인
				Join<Board, Answer> a = q.join("answerList",JoinType.LEFT); //question+answer테이블과 조인
				Join<Answer, SiteUser> u2 = a.join("author",JoinType.LEFT);	//answer+siteUser테이블과 left조인
				
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