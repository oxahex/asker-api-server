package oxahex.asker.server.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import oxahex.asker.server.domain.ask.Ask;

public class AskDto {

	@Getter
	@Setter
	public static class AskReqDto {

		@NotNull
		@Max(value = Long.MAX_VALUE, message = "올바른 값이 아닙니다.")
		private Long targetUserId;

		@NotEmpty(message = "질문 내용을 입력해주세요.")
		@Pattern(regexp = "^.{10,800}$", message = "질문 내용은 최소 10자, 최대 800자 까지 입력할 수 있습니다.")
		private String contents;
	}

	@Getter
	@Setter
	public static class AskInfoDto {

		private Long askId;
		private String contents;
		private LocalDateTime createdAt;

		public static AskInfoDto of(Ask ask) {

			AskInfoDto askInfo = new AskInfoDto();
			askInfo.setAskId(ask.getId());
			askInfo.setContents(ask.getContents());
			askInfo.setCreatedAt(ask.getCreatedAt());

			return askInfo;
		}
	}

	@Getter
	@Setter
	public static class AskListDto {

		private Page<AskInfoDto> asks;

		public static AskListDto of(Page<Ask> askList) {

			AskListDto askListDto = new AskListDto();
			askListDto.setAsks(askList.map(AskInfoDto::of));

			return askListDto;
		}
	}
}
