package oxahex.asker.server.domain.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.server.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.server.dto.AnswerDto.AnswerListDto;
import oxahex.asker.server.dto.ResponseDto;
import oxahex.asker.server.type.SortType;

@Slf4j
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

	private final AnswerService answerService;

	/**
	 * 특정 유저의 답변 목록 조회
	 *
	 * @param userId 조회하려는 유저 아이디
	 * @param page   페이지
	 * @param size   페이지 당 사이즈
	 * @param sort   정렬
	 * @return 해당 유저의 답변 목록
	 */
	@GetMapping
	@PreAuthorize("permitAll()")
	public ResponseEntity<ResponseDto<AnswerListDto>> getAnswersByUser(
			@RequestParam Long userId,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size,
			@RequestParam(defaultValue = "desc") SortType sort
	) {

		log.info("[특정 유저의 답변 목록 조회][id={}]", userId);

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort.getDirection(), "createdAt"));

		AnswerListDto answerListDto = answerService.getAnswersByUser(userId, pageRequest);

		return new ResponseEntity<>(
				new ResponseDto<>("답변 목록 조회에 성공했습니다.", answerListDto),
				HttpStatus.OK
		);
	}

	/**
	 * 특정 답변 조회
	 *
	 * @param answerId 조회할 답변 아이디
	 * @return 해당 답변 정보
	 */
	@GetMapping("/{answerId}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<ResponseDto<?>> getAnswer(
			@PathVariable Long answerId
	) {

		log.info("[답변 상세 조회][id={}]", answerId);

		AnswerInfoDto answerInfoDto = answerService.getAnswer(answerId);

		return new ResponseEntity<>(
				new ResponseDto<>("성공적으로 답변을 조회했습니다.", answerInfoDto),
				HttpStatus.OK
		);
	}
}
