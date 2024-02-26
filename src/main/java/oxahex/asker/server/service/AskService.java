package oxahex.asker.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.server.domain.ask.Ask;
import oxahex.asker.server.domain.ask.AskRepository;
import oxahex.asker.server.dto.AskDto.AskListDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class AskService {

	private final AskRepository askRepository;

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
