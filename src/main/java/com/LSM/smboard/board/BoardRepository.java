package com.LSM.smboard.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // ---------------------------
    // 전체 게시글 페이징 조회
    // ---------------------------
    @Query(value = 
        "SELECT * FROM (" +
        "   SELECT b.*, ROWNUM rnum FROM (" +
        "       SELECT * FROM boardlist b ORDER BY createdate DESC" +
        "   ) b WHERE ROWNUM <= :endRow" +
        ") WHERE rnum > :startRow",
        nativeQuery = true)
    List<Board> findBoardsWithPaging(@Param("startRow") int startRow,
                                     @Param("endRow") int endRow);

    @Query(value = "SELECT COUNT(*) FROM boardlist", nativeQuery = true)
    int countAllBoards();

    // ---------------------------
    // 제목 검색 페이징
    // ---------------------------
    @Query(value = 
        "SELECT * FROM (" +
        "   SELECT b.*, ROWNUM rnum FROM (" +
        "       SELECT * FROM boardlist b " +
        "       WHERE b.subject LIKE '%'||:kw||'%' " +
        "       ORDER BY createdate DESC" +
        "   ) b WHERE ROWNUM <= :endRow" +
        ") WHERE rnum > :startRow",
        nativeQuery = true)
    List<Board> searchBoardsBySubjectWithPaging(@Param("kw") String kw,
                                                @Param("startRow") int startRow,
                                                @Param("endRow") int endRow);

    @Query(value = "SELECT COUNT(*) FROM boardlist b WHERE b.subject LIKE '%'||:kw||'%'", nativeQuery = true)
    int countSearchBySubject(@Param("kw") String kw);

    // ---------------------------
    // 내용 검색 페이징
    // ---------------------------
    @Query(value = 
        "SELECT * FROM (" +
        "   SELECT b.*, ROWNUM rnum FROM (" +
        "       SELECT * FROM boardlist b " +
        "       WHERE b.content LIKE '%'||:kw||'%' " +
        "       ORDER BY createdate DESC" +
        "   ) b WHERE ROWNUM <= :endRow" +
        ") WHERE rnum > :startRow",
        nativeQuery = true)
    List<Board> searchBoardsByContentWithPaging(@Param("kw") String kw,
                                                @Param("startRow") int startRow,
                                                @Param("endRow") int endRow);

    @Query(value = "SELECT COUNT(*) FROM boardlist b WHERE b.content LIKE '%'||:kw||'%'", nativeQuery = true)
    int countSearchByContent(@Param("kw") String kw);

    // ---------------------------
    // 작성자 검색 페이징
    // ---------------------------
    @Query(value = 
        "SELECT * FROM (" +
        "   SELECT b.*, ROWNUM rnum FROM (" +
        "       SELECT b.* FROM boardlist b " +
        "       LEFT JOIN userinfo u1 ON b.author_id = u1.id " +
        "       WHERE u1.username LIKE '%'||:kw||'%' " +
        "       ORDER BY b.createdate DESC" +
        "   ) b WHERE ROWNUM <= :endRow" +
        ") WHERE rnum > :startRow",
        nativeQuery = true)
    List<Board> searchBoardsByAuthorWithPaging(@Param("kw") String kw,
                                               @Param("startRow") int startRow,
                                               @Param("endRow") int endRow);

    @Query(value = 
        "SELECT COUNT(*) FROM boardlist b " +
        "LEFT JOIN userinfo u1 ON b.author_id = u1.id " +
        "WHERE u1.username LIKE '%'||:kw||'%'",
        nativeQuery = true)
    int countSearchByAuthor(@Param("kw") String kw);

    // ---------------------------
    // 전체 검색 (제목 + 내용 + 작성자 + 답변 + 답변 작성자)
    // ---------------------------
    @Query(value = 
        "SELECT * FROM (" +
        "   SELECT b.*, ROWNUM rnum FROM (" +
        "       SELECT DISTINCT b.* " +
        "       FROM boardlist b " +
        "       LEFT JOIN userinfo u1 ON b.author_id = u1.id " +
        "       LEFT JOIN boardanswer a ON a.board_id = b.id " +
        "       LEFT JOIN userinfo u2 ON a.author_id = u2.id " +
        "       WHERE b.subject LIKE '%'||:kw||'%' " +
        "          OR b.content LIKE '%'||:kw||'%' " +
        "          OR u1.username LIKE '%'||:kw||'%' " +
        "          OR a.content LIKE '%'||:kw||'%' " +
        "          OR u2.username LIKE '%'||:kw||'%' " +
        "       ORDER BY b.createdate DESC" +
        "   ) b WHERE ROWNUM <= :endRow" +
        ") WHERE rnum > :startRow",
        nativeQuery = true)
    List<Board> searchBoardsWithPaging(@Param("kw") String kw,
                                       @Param("startRow") int startRow,
                                       @Param("endRow") int endRow);

    @Query(value = 
        "SELECT COUNT(DISTINCT b.id) " +
        "FROM boardlist b " +
        "LEFT JOIN userinfo u1 ON b.author_id = u1.id " +
        "LEFT JOIN boardanswer a ON a.board_id = b.id " +
        "LEFT JOIN userinfo u2 ON a.author_id = u2.id " +
        "WHERE b.subject LIKE '%'||:kw||'%' " +
        "   OR b.content LIKE '%'||:kw||'%' " +
        "   OR u1.username LIKE '%'||:kw||'%' " +
        "   OR a.content LIKE '%'||:kw||'%' " +
        "   OR u2.username LIKE '%'||:kw||'%'",
        nativeQuery = true)
    int countSearchResult(@Param("kw") String kw);
}
