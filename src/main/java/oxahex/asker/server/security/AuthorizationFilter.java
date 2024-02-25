package oxahex.asker.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import oxahex.asker.server.service.JwtTokenService;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

	private static final String HEADER = "Authorization";
	private static final String PREFIX = "Bearer ";
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {

		// Access Token이 헤더에 없는 경우 인가 처리하지 않음
		if (request.getHeader(HEADER) == null) {
			filterChain.doFilter(request, response);
			return;
		}

		log.info("[JWT 유효성 검사][{}]", request.getRequestURI());

		// Access Token
		String accessToken = request.getHeader(HEADER).replace(PREFIX, "");

		// 만료 여부 검증 및 유효 토큰 반환
		AuthUser authUser = JwtTokenService.verify(accessToken);

		Authentication authentication =
				new UsernamePasswordAuthenticationToken(
						authUser, null, authUser.getAuthorities()
				);

		// Security Context 저장
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
}
