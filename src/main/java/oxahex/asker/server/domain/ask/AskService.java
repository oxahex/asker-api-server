package oxahex.asker.server.domain.ask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.server.dto.AskDto.AskInfoDto;
import oxahex.asker.server.dto.AskDto.AskListDto;
import oxahex.asker.server.error.ServiceException;
import oxahex.asker.server.type.ErrorType;

@Slf4j
@Service
@RequiredArgsConstructor
public class AskService {

	private final AskRepository askRepository;

	/**
	 * 유저가 확인 가능한 특정 질문 상세 조회
	 *
	 * @param userId 로그인 유저 아이디
	 * @param askId  조회할 질문 아이디
	 * @return 해당 질문 정보
	 */
	@Transactional(readOnly = true)
	public AskInfoDto getAsk(Long userId, Long askId) {

		// 특정 유저가 받은 특정 질문 조회
		Ask ask = askRepository.findDispatchedAsk(askId, userId)
				.orElseThrow(() -> new ServiceException(ErrorType.ASK_FORBIDDEN));

		return AskInfoDto.of(ask);
	}

	/**
	 * 유저가 확인 가능한 질문 목록 조회
	 *
	 * @param userId      로그인 유저 아이디
	 * @param pageRequest 조회 조건
	 * @return 받은 질문 목록
	 */
	@Transactional(readOnly = true)
	public AskListDto getAskList(Long userId, PageRequest pageRequest) {

		// 해당 유저가 받은 질문 목록 조회
		Page<Ask> askList =
				askRepository.findAllDispatchedAsks(userId, pageRequest);

		return AskListDto.of(askList);
	}
}
