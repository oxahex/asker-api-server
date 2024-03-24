package oxahex.asker.server.domain.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.domain.user.UserRepository;
import oxahex.asker.server.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.server.dto.AnswerDto.AnswerListDto;
import oxahex.asker.server.error.ServiceException;
import oxahex.asker.server.type.ErrorType;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {

	private final UserRepository userRepository;
	private final AnswerRepository answerRepository;

	/**
	 * 특정 유저의 답변 조회
	 *
	 * @param userId      조회하려는 유저 아이디
	 * @param pageRequest 조회 조건
	 * @return 해당 유저의 답변 목록
	 */
	@Transactional(readOnly = true)
	public AnswerListDto getAnswersByUser(Long userId, PageRequest pageRequest) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ServiceException(ErrorType.USER_NOT_FOUND));

		// 답변 목록 조회
		Page<Answer> answerList = answerRepository.findAllByUserId(userId, pageRequest);

		return AnswerListDto.of(user, answerList);
	}

	/**
	 * 특정 답변 조회
	 *
	 * @param answerId 조회할 답변 아이디
	 * @return 해당 답변 정보
	 */
	@Transactional(readOnly = true)
	public AnswerInfoDto getAnswer(Long answerId) {

		Answer answer = answerRepository.findById(answerId)
				.orElseThrow(() -> new ServiceException(ErrorType.ANSWER_NOT_FOUND));

		return AnswerInfoDto.of(answer);
	}
}
