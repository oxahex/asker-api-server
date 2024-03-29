package oxahex.asker.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import oxahex.asker.server.dto.ResponseDto;

@Slf4j
public class ResponseUtil {

	public static void success(
			ObjectMapper objectMapper,
			HttpServletResponse response,
			HttpStatus status,
			String message,
			Object dto
	) {
		try {

			ResponseDto<?> responseDto = new ResponseDto<>(message, dto);

			String responseBody = objectMapper.writeValueAsString(responseDto);

			response.setStatus(status.value());
			response.setCharacterEncoding("utf-8");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			response.getWriter().write(responseBody);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	// TODO: Error Type Enum을 인자로 받도록 리팩토링 하면 어떨지?
	public static void failure(
			ObjectMapper objectMapper,
			HttpServletResponse response,
			HttpStatus status,
			String message
	) {
		try {

			ResponseDto<?> responseDto = new ResponseDto<>(message, null);

			String responseBody = objectMapper.writeValueAsString(responseDto);

			response.setStatus(status.value());
			response.setCharacterEncoding("utf-8");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			response.getWriter().write(responseBody);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
