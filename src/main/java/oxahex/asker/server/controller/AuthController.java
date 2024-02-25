package oxahex.asker.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.server.dto.JoinDto.JoinReqDto;
import oxahex.asker.server.dto.JoinDto.JoinResDto;
import oxahex.asker.server.dto.ResponseDto;
import oxahex.asker.server.service.AuthService;

@Slf4j
@RestController
@PreAuthorize("permitAll()")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final String HEADER = "Authorization";
	private static final String PREFIX = "Bearer ";
	private final AuthService authService;

	/**
	 * 이메일 회원가입
	 *
	 * @param joinReqDto 이메일 회원가입 요청 Body
	 * @return 회원가입 유저 정보
	 */
	@PostMapping("/join")
	public ResponseEntity<ResponseDto<JoinResDto>> joinWithEmail(
			@RequestBody @Valid JoinReqDto joinReqDto
	) {

		log.info("[회원가입 요청][email={}]", joinReqDto.getEmail());

		JoinResDto joinResDto = authService.createUser(joinReqDto);

		return new ResponseEntity<>(
				new ResponseDto<>("회원 가입이 완료되었습니다.", joinResDto),
				HttpStatus.CREATED
		);
	}
}
