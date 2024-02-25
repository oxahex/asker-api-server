package oxahex.asker.server.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import oxahex.asker.server.type.ErrorType;

@Getter
public class AccessException extends AccessDeniedException {

	private final HttpStatus httpStatus = HttpStatus.FORBIDDEN;
	String errorMessage;

	public AccessException(ErrorType errorType) {
		super(errorType.getErrorMessage());
		this.errorMessage = errorType.getErrorMessage();
	}
}
