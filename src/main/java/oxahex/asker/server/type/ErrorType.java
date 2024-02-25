package oxahex.asker.server.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

	// 인증/인가
	AUTHENTICATION_FAILURE(HttpStatus.UNAUTHORIZED, "인증 정보가 없거나 올바르지 않습니다."),
	AUTHORIZATION_FAILURE(HttpStatus.FORBIDDEN, "권한이 없습니다."),
	TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 토큰입니다.");

	private final HttpStatus httpStatus;
	private final String errorMessage;
}
