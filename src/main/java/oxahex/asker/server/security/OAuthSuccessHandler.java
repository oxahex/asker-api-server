package oxahex.asker.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import oxahex.asker.server.cache.RedisRepository;
import oxahex.asker.server.domain.auth.JwtTokenService;
import oxahex.asker.server.dto.LoginDto.LoginResDto;
import oxahex.asker.server.type.JwtTokenType;
import oxahex.asker.server.type.RedisType;
import oxahex.asker.server.utils.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

	private final RedisRepository redisRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) throws IOException, ServletException {

		log.info("[OAuth 인증 성공]");
		AuthUser authUser = (AuthUser) authentication.getPrincipal();

		// Access Token, Refresh Token 생성
		String accessToken = JwtTokenService.create(authUser, JwtTokenType.ACCESS_TOKEN);
		String refreshToken = JwtTokenService.create(authUser, JwtTokenType.REFRESH_TOKEN);

		// Refresh Token Redis 캐싱
		redisRepository.save(RedisType.REFRESH_TOKEN, authUser.getUsername(), refreshToken);

		// Refresh Token Set-Cookie
		ResponseCookie cookie = ResponseCookie
				.from("refreshToken", refreshToken)
				.maxAge(JwtTokenType.REFRESH_TOKEN.getExpireTime())
				.path("/api/auth/token")
				.secure(true)
				.sameSite("localhost")
				.httpOnly(true)
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		// Token, 로그인 유저 정보 응답
		ResponseUtil.success(
				objectMapper,
				response,
				HttpStatus.OK,
				"정상적으로 로그인 되었습니다.",
				new LoginResDto(authUser.getUser(), accessToken)
		);
	}
}
