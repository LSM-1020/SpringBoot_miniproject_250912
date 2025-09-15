package com.LSM.smboard.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Integer>{
public Board findBySubject(String subject);
	
	public Board findBySubjectAndContent(String subject, String content);
	
	public List<Board> findBySubjectLike(String keyword);
	
	//페이징 관련
	//public Page<Question> findAll(Pageable pageable);
	@Query(
	         value = "SELECT * FROM ( " +
	                 " SELECT b.*, ROWNUM rnum FROM ( " +
	                 "   SELECT * FROM boardlist ORDER BY CREATEDATE DESC " +
	                 " ) b WHERE ROWNUM <= :endRow " +
	                 ") WHERE rnum > :startRow",
	         nativeQuery = true)
	    List<Board> findBoardsWithPaging(@Param("startRow") int startRow,
	                                           @Param("endRow") int endRow);
	//검색을 조회하는 페이징
	@Query(value = 
		       "SELECT * FROM ( " +
		       "   SELECT b.*, ROWNUM rnum FROM ( " +
		       "       SELECT DISTINCT b.* " +
		       "       FROM boardlist b " +
		       "       LEFT OUTER JOIN userinfo u1 ON b.author_id = u1.id " +
		       "       LEFT OUTER JOIN boardanswer a ON a.board_id = b.id " +
		       "       LEFT OUTER JOIN userinfo u2 ON a.author_id = u2.id " +
		       "       WHERE b.subject LIKE '%'||:kw||'%' " +
		       "          OR b.content LIKE '%'||:kw||'%'" +
		       "          OR u1.username LIKE '%'||:kw||'%'" +
		       "          OR a.content LIKE '%'||:kw||'%'" +
		       "          OR u2.username LIKE '%'||:kw||'%'" +
		       "       ORDER BY b.createdate DESC " +
		       "   ) b WHERE ROWNUM <= :endRow " +
		       ") WHERE rnum > :startRow", 
		       nativeQuery = true)
	 List<Board> searchBoardsWithPaging(@Param("kw") String kw,@Param("startRow") int startRow,
             @Param("endRow") int endRow);
	//검색 결과 총 갯수 반환
	@Query(value =  
    "   SELECT COUNT(DISTINCT b.id) " +
    "       FROM boardlist b " +
    "       LEFT OUTER JOIN userinfo u1 ON b.author_id = u1.id " +
    "       LEFT OUTER JOIN boardanswer a ON a.board_id = b.id " +
    "       LEFT OUTER JOIN userinfo u2 ON a.author_id = u2.id " +
    "       WHERE b.subject LIKE '%'||:kw||'%' " +
    "          OR b.content LIKE '%'||:kw||'%'" +
    "          OR u1.username LIKE '%'||:kw||'%'" +
    "          OR a.content LIKE '%'||:kw||'%'" +
    "          OR u2.username LIKE '%'||:kw||'%'",
           nativeQuery = true)
	public int countSearchResult(@Param("kw") String kw);
	
	//Page<Question> findAll(Specification<Question> spec, Pageable pageable);
	
}


