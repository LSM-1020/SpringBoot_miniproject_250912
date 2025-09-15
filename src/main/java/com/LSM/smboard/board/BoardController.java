package com.LSM.smboard.board;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.LSM.smboard.answer.AnswerForm;
import com.LSM.smboard.answer.AnswerService;
import com.LSM.smboard.user.SiteUser;
import com.LSM.smboard.user.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/board")
public class BoardController {
	
	 private final AnswerService answerService;
		

		
		@Autowired
		private BoardService boardService;
		
		@Autowired
		private UserService userService;


	    BoardController(AnswerService answerService) {
	        this.answerService = answerService;
	    }
		
	

//		@GetMapping(value = "/detail/{id}") //파라미터이름 없이 값만 넘어왔을때 처리
//		public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
//			boardService.hit(boardService.getBoard(id));//조회수 증가
//			
//			//service에 3을 넣어서 호출
//			Board board = boardService.getBoard(id);
//			model.addAttribute("board", board);
//			return "board_detail"; //타임리프 html의 이름
//		}
//	    @GetMapping("/detail/{id}")
//	    public String boardDetail(@PathVariable("id") Integer id,
//	                              @RequestParam(value = "edit", required = false) Integer editAnswerId,
//	                              Model model) {
//
//	        Board board = boardService.getBoard(id);
//	        model.addAttribute("board", board);
//
//	        AnswerForm answerForm = new AnswerForm();
//	        model.addAttribute("answerForm", answerForm);
//
//	        // 인라인 편집용 플래그 설정
//	        if (editAnswerId != null) {
//	            board.getAnswerList().forEach(answer -> {
//	                if (answer.getId().equals(editAnswerId)) {
//	                    answer.setEditMode(true); // 편집 모드 활성화
//	                    answerForm.setContent(answer.getContent());
//	                } else {
//	                    answer.setEditMode(false);
//	                }
//	            });
//	        }
//
//	        return "board_detail";
//	    }
	    @GetMapping(value = "/detail/{id}") //파라미터이름 없이 값만 넘어왔을때 처리
		public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
			boardService.hit(boardService.getBoard(id));//조회수 증가
			
			//service에 3을 넣어서 호출
			Board board = boardService.getBoard(id);
			model.addAttribute("board", board);
			return "board_detail"; //타임리프 html의 이름
		}
	    
	    
	    
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value = "/create") //질문 등록 폼만 매핑해주는 메서드->GET
		public String boardCreate(BoardForm boardForm) {
			return "board_form"; //질문 등록하는 폼 페이지 이름
		}
		
		@PreAuthorize("isAuthenticated()") //권한 설정, form에서 action으로 넘어오지 않으면 권한인증이 안됨
		@PostMapping(value = "/create") //질문 내용을 DB에 저장하는 메서드->POST
		public String boardCreate(@Valid BoardForm boardForm, BindingResult bindingResult, Principal principal, Model model) {		
			
			if(bindingResult.hasErrors()) { //참이면 -> 유효성 체크에서 에러 발생
				return "board_form"; //에러 발생 시 다시 질문 등록 폼으로 이동
			}
			SiteUser siteUser = userService.getUser(principal.getName());
			//현재 로그인된 username으로 siteuser
			boardService.create(boardForm.getSubject(), boardForm.getContent(), siteUser); //질문 DB에 등록하기
			
			return "redirect:/board/list"; //질문 리스트로 이동->반드시 redirect
		}
		
		@GetMapping("/list")
		public String list(Model model,
		                   @RequestParam(value = "page", defaultValue = "0") int page,
		                   @RequestParam(value = "kw", defaultValue = "") String kw) {

		    int pageSize = 10;          // 한 페이지 글 수
		    int displayPageCount = 10;   // 한 그룹에 표시할 페이지 수

		    Page<Board> paging = boardService.getPageBoards(page, kw);

		    int totalPages = paging.getTotalPages();

		 // 현재 페이지 그룹 계산
		    int currentGroup = page / displayPageCount;      
		    int startPage = currentGroup * displayPageCount; 
		    int endPage = Math.min(startPage + displayPageCount - 1, totalPages - 1);

		    // 이전/다음 그룹 이동용 페이지 번호
		    int prevGroupPage = Math.max(startPage - 1, 0);                  
		    int nextGroupPage = Math.min(endPage + 1, totalPages - 1);       

		    // 처음과 마지막 페이지
		    int firstPage = 0;
		    int lastPage = totalPages - 1;

		    model.addAttribute("paging", paging);
		    model.addAttribute("kw", kw);
		    model.addAttribute("startPage", startPage);
		    model.addAttribute("endPage", endPage);
		    model.addAttribute("prevGroupPage", prevGroupPage);
		    model.addAttribute("nextGroupPage", nextGroupPage);
		    model.addAttribute("firstPage", firstPage);
		    model.addAttribute("lastPage", lastPage);

		    return "board_list";
		}
		
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/modify/{id}")
		public String boardModify(BoardForm boardForm, @PathVariable("id") Integer id, Principal principal, Model model) {
			Board board = boardService.getBoard(id);//아이디에 해당하는 엔티티 반환->수정하려는 글의 엔티티
			//글쓴 유저와 로그인한 유저의 동일 여부 재확인
			if(!board.getAuthor().getUsername().equals(principal.getName())) { //참이면 수정권한 없음
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다");
			}
			
			boardForm.setSubject(board.getSubject());
			boardForm.setContent(board.getContent());
			model.addAttribute("board",board);
			return "board_form";
		}
		@PreAuthorize("isAuthenticated()")
		@PostMapping(value="/modify/{id}")
		public String boardModify(@Valid BoardForm boardForm, BindingResult bindingResult,Principal principal,@PathVariable("id") Integer id) {
			if(bindingResult.hasErrors()) {
				return "board_form";
			}
			
			Board board = boardService.getBoard(id);
			//글쓴 유저와 로그인한 유저의 동일 여부 재확인
					if(!board.getAuthor().getUsername().equals(principal.getName())) { //참이면 수정권한 없음
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다");
					}
					boardService.modify(board,boardForm.getSubject(),boardForm.getContent());
			return String.format("redirect:/board/detail/%s", id);
		}
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/delete/{id}")
		public String delete(Principal principal,@PathVariable("id") Integer id) {
			
			Board board = boardService.getBoard(id);
			//글쓴 유저와 로그인한 유저의 동일 여부 재확인
					if(!board.getAuthor().getUsername().equals(principal.getName())) { //참이면 수정권한 없음
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다");
					}
					boardService.delete(board);
					return "redirect:/board/list";
		}
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/vote/{id}")
		public String boardVote(Principal principal,@PathVariable("id") Integer id) {
			Board board = boardService.getBoard(id);
			SiteUser siteUser = userService.getUser(principal.getName());
			
			boardService.vote(board, siteUser);
			
					return String.format("redirect:/board/detail/%s", id);
		}
		
		@PreAuthorize("isAuthenticated()")
		@GetMapping(value="/disvote/{id}")
		public String boarddisVote(Principal principal,@PathVariable("id") Integer id) {
			Board board = boardService.getBoard(id);
			SiteUser siteUser = userService.getUser(principal.getName());
			
			boardService.disvote(board, siteUser);
			
					return String.format("redirect:/board/detail/%s", id);
		}
		
		
	}
