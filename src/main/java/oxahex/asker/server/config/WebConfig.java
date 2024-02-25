package oxahex.asker.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import oxahex.asker.server.type.SortTypeConverter;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final DateTimeFormatter DATE_TIME_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	private static final DateTimeFormatter DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * 요청 파라미터 Enum 값 컨버터 빈 등록
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new SortTypeConverter());
	}

	/**
	 * 패스워드 암호화를 위한 인코더 빈 등록
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		log.debug("[Bean 등록 - PasswordEncoder]");
		return new BCryptPasswordEncoder();
	}

	/**
	 * ObjectMapper 빈 등록
	 * <li>응답 시 날짜/시간 포맷팅 추가
	 */
	@Bean
	public ObjectMapper objectMapper() {

		log.debug("[Bean 등록 - ObjectMapper]");

		ObjectMapper objectMapper = new ObjectMapper();

		// 날짜/시간 필드 기본 모듈
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class,
				new LocalDateTimeSerializer(DATE_TIME_FORMAT));
		javaTimeModule.addSerializer(LocalDate.class,
				new LocalDateSerializer(DATE_FORMAT));

		objectMapper.registerModule(javaTimeModule);

		return objectMapper;
	}
}
