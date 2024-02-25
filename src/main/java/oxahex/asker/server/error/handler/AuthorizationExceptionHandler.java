package oxahex.asker.server.error.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import oxahex.asker.server.type.ErrorType;
import oxahex.asker.server.utils.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationExceptionHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(
			HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException
	) throws IOException, ServletException {

		log.error("[인가 오류][msg={}]", accessDeniedException.getMessage());

		ResponseUtil.failure(
				objectMapper,
				response,
				ErrorType.AUTHORIZATION_FAILURE.getHttpStatus(),
				ErrorType.AUTHORIZATION_FAILURE.getErrorMessage()
		);
	}
}
