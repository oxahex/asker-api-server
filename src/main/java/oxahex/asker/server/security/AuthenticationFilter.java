package oxahex.asker.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import oxahex.asker.server.cache.RedisRepository;
import oxahex.asker.server.dto.LoginDto.LoginReqDto;
import oxahex.asker.server.dto.LoginDto.LoginResDto;
import oxahex.asker.server.error.AuthException;
import oxahex.asker.server.service.JwtTokenService;
import oxahex.asker.server.type.ErrorType;
import oxahex.asker.server.type.JwtTokenType;
import oxahex.asker.server.type.RedisType;
import oxahex.asker.server.utils.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final ObjectMapper objectMapper;
	private final RedisRepository redisRepository;

	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request,
			HttpServletResponse response
	) throws AuthenticationException {

		try {

			LoginReqDto loginReqDto =
					objectMapper.readValue(request.getInputStream(), LoginReqDto.class);

			log.info("[이메일 로그인 시도][{}]", loginReqDto.getEmail());

			// 강제 로그인 처리
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(
							loginReqDto.getEmail(), loginReqDto.getPassword()
					);

			// 검증 성공 시 진행
			return this.getAuthenticationManager().authenticate(authenticationToken);

		} catch (Exception e) {

			log.error("[ERROR] 이메일 로그인 에러");
			throw new AuthException(ErrorType.AUTHENTICATION_FAILURE);
		}
	}

	@Override
	protected void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult
	) throws IOException, ServletException {

		log.info("[이메일 인증 성공]");

		AuthUser authUser = (AuthUser) authResult.getPrincipal();

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

	@Override
	protected void unsuccessfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException failed
	) throws IOException, ServletException {

		log.info("[이메일 인증 실패]");

		ResponseUtil.failure(
				objectMapper,
				response,
				HttpStatus.UNAUTHORIZED,
				failed.getMessage()
		);
	}
}
