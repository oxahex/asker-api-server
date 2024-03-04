package oxahex.asker.server.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {

	// 최대 30자
	@NotEmpty(message = "로그인에 사용할 이메일을 입력해주세요.")
	@Pattern(regexp = "^[a-zA-Z0-9+-_.]{1,30}@[a-zA-Z0-9-]+\\.[a-zA-Z]+$", message = "올바른 이메일 형식이 아닙니다(이메일 아이디는 최대 30자까지 입력 가능합니다).")
	@Size(max = 50, message = "50자 미만의 이메일을 입력해주세요.")
	private String email;
}
