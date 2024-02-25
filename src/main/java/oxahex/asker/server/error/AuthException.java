package oxahex.asker.server.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import oxahex.asker.server.type.ErrorType;

@Getter
public class AuthException extends AuthenticationException {

	private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
	String errorMessage;

	public AuthException(ErrorType errorType) {
		super(errorType.getErrorMessage());
		this.errorMessage = errorType.getErrorMessage();
	}
}
