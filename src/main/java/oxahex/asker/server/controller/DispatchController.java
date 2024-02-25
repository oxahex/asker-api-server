package oxahex.asker.server.controller;

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
import oxahex.asker.server.dto.AskDto.AskInfoDto;
import oxahex.asker.server.dto.AskDto.AskReqDto;
import oxahex.asker.server.dto.ResponseDto;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.service.DispatchService;

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

		log.info("[질문하기][email={}]", authUser.getUsername());

		AskInfoDto askInfoDto = dispatchService.dispatchAsk(authUser, askReqDto);

		return new ResponseEntity<>(
				new ResponseDto<>("성공적으로 질문했습니다.", askInfoDto),
				HttpStatus.CREATED
		);
	}
}
