package oxahex.asker.server.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.server.dto.EmailDto;
import oxahex.asker.server.dto.JoinDto.JoinReqDto;
import oxahex.asker.server.dto.JoinDto.JoinResDto;
import oxahex.asker.server.dto.ResponseDto;
import oxahex.asker.server.error.ServiceException;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.service.AuthService;
import oxahex.asker.server.service.JwtTokenService;
import oxahex.asker.server.type.ErrorType;
import oxahex.asker.server.type.ProviderType;

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
	@PostMapping("/join/email")
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
	 * @param emailDto 가입 요청 이메일
	 * @return 발송 결과
	 */
	@PostMapping("/email/check")
	@PreAuthorize("permitAll()")
	public ResponseEntity<ResponseDto<String>> preCheckEmail(
			@RequestBody EmailDto emailDto
	) {

		log.info("[이메일 사전 확인]");
		String email = authService.sendEmailCode(emailDto);

		return new ResponseEntity<>(
				new ResponseDto<>("이메일 코드가 전송되었습니다.", email),
				HttpStatus.OK
		);
	}

	@GetMapping("/oauth")
	@PreAuthorize("permitAll()")
	public void joinWithOAuth(
			HttpServletResponse response,
			@RequestParam ProviderType provider
	) throws IOException {

		switch (provider) {
			case GOOGLE -> response.sendRedirect("/oauth2/authorization/google");
			case TWITTER -> response.sendRedirect("https://accounts.google.com/o/oauth2/v2/authd");
			default -> throw new ServiceException(ErrorType.PROVIDER_INVALID);
		}
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
