package oxahex.asker.server.error.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import oxahex.asker.server.type.ErrorType;
import oxahex.asker.server.utils.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;
	
	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException
	) throws IOException, ServletException {

		log.error("[인증 오류][msg={}]", authException.getMessage());

		ResponseUtil.failure(
				objectMapper,
				response,
				ErrorType.AUTHENTICATION_FAILURE.getHttpStatus(),
				ErrorType.AUTHENTICATION_FAILURE.getErrorMessage()
		);
	}
}
