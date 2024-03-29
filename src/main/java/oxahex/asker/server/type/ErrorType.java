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
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
	TOKEN_REISSUE_FAILURE(HttpStatus.UNAUTHORIZED, "토큰 재발급에 실패했습니다. 로그인이 필요합니다."),

	// OAuth
	PROVIDER_INVALID(HttpStatus.BAD_REQUEST, "올바른 프로바이더가 아닙니다."),
	OAUTH_EMAIL_CONFLICT(HttpStatus.BAD_REQUEST, "가입된 이메일입니다. 이메일로 로그인해주세요."),

	// 이메일
	EMAIL_CONFLICT(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
	EMAIL_CODE_EXPIRED(HttpStatus.FORBIDDEN, "이메일 인증 시간이 만료되었습니다."),
	EMAIL_CODE_MIS_MATCH(HttpStatus.FORBIDDEN, "이메일 인증 코드가 올바르지 않습니다."),

	// 유저
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

	// 질문
	ASK_FORBIDDEN(HttpStatus.FORBIDDEN, "답변 권한이 없습니다."),

	// 답변
	ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 답변입니다.");

	private final HttpStatus httpStatus;
	private final String errorMessage;
}
