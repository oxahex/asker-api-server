package oxahex.asker.server.domain.ask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.server.dto.AskDto.AskInfoDto;
import oxahex.asker.server.dto.AskDto.AskListDto;
import oxahex.asker.server.dto.ResponseDto;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.type.SortType;

@Slf4j
@RestController
@RequestMapping("/api/asks")
@RequiredArgsConstructor
public class AskController {

	private final AskService askService;

	/**
	 * 로그인 유저가 받은 질문 내역 조회
	 *
	 * @param authUser 로그인 유저
	 * @param page     페이지 번호
	 * @param size     페이지 당 크기
	 * @param sort     정렬 타입
	 * @return 받은 질문 내역
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<AskListDto>> getDispatchedAskList(
			@AuthenticationPrincipal AuthUser authUser,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size,
			@RequestParam(defaultValue = "desc") SortType sort
	) {

		log.info("[받은 질문 목록 조회][email={}]", authUser.getUsername());

		PageRequest pageRequest =
				PageRequest.of(page, size, Sort.by(sort.getDirection(), "createdAt"));

		AskListDto askListDto = askService.getAskList(authUser.getUser().getId(), pageRequest);

		return new ResponseEntity<>(
				new ResponseDto<>("받은 질문 내역을 조회했습니다.", askListDto),
				HttpStatus.OK
		);
	}

	/**
	 * 특정 질문 상세 조회
	 *
	 * @param authUser 로그인 유저
	 * @param askId    조회할 질문 아이디
	 * @return 해당 질문 상세 정보
	 */
	@GetMapping("/{askId}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<AskInfoDto>> getDispatchedAsk(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long askId
	) {

		log.info("[받은 질문 상세 조회][email={}][askId={}]", authUser.getUsername(), askId);

		AskInfoDto askInfoDto = askService.getAsk(authUser.getUser().getId(), askId);

		return new ResponseEntity<>(
				new ResponseDto<>("해당 질문을 조회했습니다.", askInfoDto),
				HttpStatus.OK
		);
	}
}
