package oxahex.asker.server.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.server.domain.ask.Ask;
import oxahex.asker.server.domain.ask.AskRepository;
import oxahex.asker.server.domain.dispatch.Dispatch;
import oxahex.asker.server.domain.dispatch.DispatchRepository;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.domain.user.UserRepository;
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
				.answerUser(targetUser)
				.ask(ask)
				.build());

		// TODO: 알림 생성

		return AskInfoDto.of(ask);
	}

}
