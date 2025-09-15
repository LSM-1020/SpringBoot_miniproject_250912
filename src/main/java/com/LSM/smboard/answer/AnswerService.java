package com.LSM.smboard.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LSM.smboard.DataNotFoundException;
import com.LSM.smboard.board.Board;

import com.LSM.smboard.user.SiteUser;

@Service
public class AnswerService {
	@Autowired
	private AnswerRepository answerRepository;
	
	public Answer create(Board board, String content, SiteUser author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setBoard(board);
		answer.setCreatedate(LocalDateTime.now());
		answer.setAuthor(author);
		answerRepository.save(answer);
		return answer;
	}
	public Answer getAnswer(Integer id) { //기본키인 답변id를 인수로 넣어주면 해당 엔티티 반환
		Optional<Answer> _answer = answerRepository.findById(id); //기본키를 엔티티로 조회
		if(_answer.isPresent()) {
			return _answer.get(); //해당 answer엔티티 반환
		} else {
			throw new DataNotFoundException("해당 답변이 존재하지 않습니다");
		}
	}
	
	public void modify(Answer answer, String content) { //답변 수정하기
		answer.setContent(content);
		answer.setModifydate(LocalDateTime.now()); //답변수정 일시
		answerRepository.save(answer);
	}
	public void delete(Answer answer) {
		answerRepository.delete(answer);
	}
	public void vote(Answer answer, SiteUser siteUser) { //업데이트문으로 만들어줘야함
		answer.getVoter().add(siteUser);
		//question은 추천을 받은 글의 번호로 조회한 엔티티
		//question의 멤버인 voter를 get해서 voter에 추천을 누른 유저의 엔티티를 추가
		answerRepository.save(answer); //추천한 유저수가 변경된 질문 엔티티를 다시 save해서 갱신
	}
	public void disvote(Answer answer, SiteUser siteUser) { //업데이트문으로 만들어줘야함
		answer.getDisvoter().add(siteUser);
		//question은 추천을 받은 글의 번호로 조회한 엔티티
		//question의 멤버인 voter를 get해서 voter에 추천을 누른 유저의 엔티티를 추가
		answerRepository.save(answer); //추천한 유저수가 변경된 질문 엔티티를 다시 save해서 갱신
	}
}
