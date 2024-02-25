package oxahex.asker.server.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import oxahex.asker.server.domain.answer.Answer;
import oxahex.asker.server.dto.AskDto.AskInfoDto;

public class AnswerDto {

	@Getter
	@Setter
	public static class AnswerReqDto {

		@NotNull
		@Max(value = Long.MAX_VALUE, message = "올바른 값이 아닙니다.")
		private Long askId;

		@NotEmpty(message = "답변 내용을 입력해주세요.")
		@Pattern(regexp = "^.{10,800}$", message = "답변 내용은 최소 10자, 최대 800자 까지 입력할 수 있습니다.")
		private String contents;
	}

	@Getter
	@Setter
	public static class AnswerInfoDto {

		private Long answerId;
		private String contents;
		private AskInfoDto ask;
		private LocalDateTime createdAt;

		public static AnswerInfoDto of(Answer answer) {

			AnswerInfoDto answerInfo = new AnswerInfoDto();
			answerInfo.setAnswerId(answer.getId());
			answerInfo.setContents(answer.getContents());
			answerInfo.setAsk(AskInfoDto.of(answer.getAsk()));
			answerInfo.setCreatedAt(answer.getCreatedAt());

			return answerInfo;
		}
	}
}
