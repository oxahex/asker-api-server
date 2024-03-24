package oxahex.asker.server.domain.dispatch;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.server.domain.answer.Answer;
import oxahex.asker.server.domain.answer.AnswerRepository;
import oxahex.asker.server.domain.ask.Ask;
import oxahex.asker.server.domain.ask.AskRepository;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.domain.user.UserRepository;
import oxahex.asker.server.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.server.dto.AnswerDto.AnswerReqDto;
import oxahex.asker.server.dto.AskDto.AskInfoDto;
import oxahex.asker.server.dto.AskDto.AskReqDto;
import oxahex.asker.server.error.ServiceException;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.type.AskType;
import oxahex.asker.server.type.ErrorType;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

	private final UserRepository userRepository;
	private final AskRepository askRepository;
	private final AnswerRepository answerRepository;
	private final DispatchRepository dispatchRepository;

	/**
	 * 질문 생성 및 전송, 알림 생성
	 *
	 * @param authUser  로그인 유저
	 * @param askReqDto 질문 요청 내용
	 * @return 생성된 질문
	 */
	@Transactional
	public AskInfoDto dispatchAsk(AuthUser authUser, AskReqDto askReqDto) {

		log.info("[질문 생성 및 전송]");

		// 익명 질문인 경우 User = null
		User askUser = Optional.ofNullable(authUser)
				.flatMap(it -> userRepository.findById(it.getUser().getId()))
				.orElse(null);

		// 질문 받을 유저
		User targetUser = userRepository.findById(askReqDto.getTargetUserId())
				.orElseThrow(() -> new ServiceException(ErrorType.USER_NOT_FOUND));

		// 질문 생성
		Ask ask = askRepository.save(Ask.builder()
				.askUser(askUser)
				.contents(askReqDto.getContents())
				.askType(AskType.USER)
				.build());

		// 전송 내역 생성
		Dispatch dispatch = dispatchRepository.save(Dispatch.builder()
				.receiveUser(targetUser)
				.ask(ask)
				.build());

		// TODO: 알림 생성

		return AskInfoDto.of(ask);
	}

	/**
	 * 답변 생성
	 *
	 * @param authUser     로그인 유저
	 * @param answerReqDto 답변 생성 요청 데이터
	 * @return 생성한 답변
	 */
	@Transactional
	public AnswerInfoDto dispatchAnswer(AuthUser authUser, AnswerReqDto answerReqDto) {

		log.info("[답변 생성]");

		// 답변 가능한 질문인지 확인
		Ask ask = askRepository.findDispatchedAsk(answerReqDto.getAskId(), authUser.getUser().getId())
				.orElseThrow(() -> new ServiceException(ErrorType.ASK_FORBIDDEN));

		// 답변 생성
		Answer answer = answerRepository.save(Answer.builder()
				.answerUser(authUser.getUser())
				.ask(ask)
				.contents(answerReqDto.getContents())
				.build());

		// TODO: 로그인 유저 질문인 경우 답변 생성 시 알림 생성

		// TODO: 답변 생성 시 Elasticsearch 저장

		return AnswerInfoDto.of(answer);
	}
}
