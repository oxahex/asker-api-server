package oxahex.asker.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.server.dto.JoinDto.JoinReqDto;
import oxahex.asker.server.dto.JoinDto.JoinResDto;
import oxahex.asker.server.dto.ResponseDto;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.service.AuthService;
import oxahex.asker.server.service.JwtTokenService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final String COOKIE_KEY = "refreshToken";

	private final AuthService authService;

	/**
	 * 이메일 회원가입
	 *
	 * @param joinReqDto 이메일 회원가입 요청 Body
	 * @return 회원가입 유저 정보
	 */
	@PostMapping("/join")
	@PreAuthorize("permitAll()")
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

	/**
	 * 이메일 사전 검증 및 인증 코드 발송
	 *
	 * @param email 가입 요청 이메일
	 * @return 발송 결과
	 */
	@PostMapping("/email/code")
	@PreAuthorize("permitAll()")
	public ResponseEntity<ResponseDto<?>> preCheckEmail(
			@RequestBody String email
	) {

		log.info("[이메일 사전 확인][email={}]", email);
		authService.sendEmailCode(email);

		return new ResponseEntity<>(
				new ResponseDto<>("이메일 코드가 전송되었습니다.", null),
				HttpStatus.OK
		);
	}

	/**
	 * JWT Access Token 재발급
	 * <p> Access Token 만료 시 프론트에서 Authorization Header에 Refresh Token 전송
	 *
	 * @param refreshToken 쿠키 Refresh Token 값
	 * @return JWT Access Token
	 */
	@PostMapping(path = "/token")
	public ResponseEntity<ResponseDto<String>> reIssueAccessToken(
			@CookieValue(name = COOKIE_KEY) String refreshToken
	) {

		log.info("[JWT Access Token 재발급]");
		AuthUser authUser = JwtTokenService.verify(refreshToken);
		String accessToken = authService.reIssueAccessToken(authUser, refreshToken);

		return new ResponseEntity<>(
				new ResponseDto<>("토큰이 정상적으로 재발급되었습니다.", accessToken),
				HttpStatus.CREATED
		);
	}
}
