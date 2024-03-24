package oxahex.asker.server.domain.dispatch;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.server.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.server.dto.AnswerDto.AnswerReqDto;
import oxahex.asker.server.dto.AskDto.AskInfoDto;
import oxahex.asker.server.dto.AskDto.AskReqDto;
import oxahex.asker.server.dto.ResponseDto;
import oxahex.asker.server.security.AuthUser;

@Slf4j
@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

	private final DispatchService dispatchService;

	/**
	 * 특정 유저에게 질문
	 *
	 * @param askReqDto 질문 요청 Body
	 * @param authUser  로그인 유저
	 * @return 질문 내용
	 */
	@PostMapping("/ask")
	@PreAuthorize("permitAll()")
	public ResponseEntity<ResponseDto<AskInfoDto>> dispatchAsk(
			@RequestBody @Valid AskReqDto askReqDto,
			@AuthenticationPrincipal AuthUser authUser
	) {

		log.info("[질문하기]");

		AskInfoDto askInfoDto = dispatchService.dispatchAsk(authUser, askReqDto);

		return new ResponseEntity<>(
				new ResponseDto<>("성공적으로 질문했습니다.", askInfoDto),
				HttpStatus.CREATED
		);
	}

	/**
	 * 특정 질문에 답변하기
	 *
	 * @param answerReqDto 답변 생성 요청 Body
	 * @param authUser     로그인 유저
	 * @return 생성한 답변 및 질문 정보 응답
	 */
	@PostMapping("/answer")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<AnswerInfoDto>> dispatchAnswer(
			@RequestBody @Valid AnswerReqDto answerReqDto,
			@AuthenticationPrincipal AuthUser authUser
	) {

		log.info("[답변하기]");

		AnswerInfoDto answerInfoDto = dispatchService.dispatchAnswer(authUser, answerReqDto);

		return new ResponseEntity<>(
				new ResponseDto<>("성공적으로 답변했습니다.", answerInfoDto),
				HttpStatus.CREATED
		);
	}
}
