package oxahex.asker.server.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import oxahex.asker.server.type.ErrorType;

@Getter
public class ServiceException extends RuntimeException {

	HttpStatus httpStatus;
	String errorMessage;

	public ServiceException(ErrorType errorType) {
		this.httpStatus = errorType.getHttpStatus();
		this.errorMessage = errorType.getErrorMessage();
	}
}
