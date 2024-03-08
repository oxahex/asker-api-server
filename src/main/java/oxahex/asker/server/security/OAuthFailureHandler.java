package oxahex.asker.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import oxahex.asker.server.utils.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class OAuthFailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception
	) throws IOException, ServletException {

		log.info("[OAuth 인증 실패]");
		ResponseUtil.failure(
				objectMapper,
				response,
				HttpStatus.UNAUTHORIZED,
				exception.getMessage()
		);
	}
}
