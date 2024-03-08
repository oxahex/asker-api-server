package oxahex.asker.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.domain.user.UserRepository;
import oxahex.asker.server.error.ServiceException;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.type.ErrorType;
import oxahex.asker.server.type.ProviderType;
import oxahex.asker.server.type.RoleType;
import oxahex.asker.server.utils.RandomCodeUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		log.info("[OAuth 인증 후 가입 회원 조회]");

		ProviderType providerType =
				ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

		OAuth2User oAuth2User = super.loadUser(userRequest);

		String email = oAuth2User.getAttribute("email");

		// 해당 provider로 가입된 유저인 경우 해당 유저 반환
		// 해당 이메일로 가입된 유저가 없으면 새 유저 생성
		User user = userRepository.findOAuthUser(email, providerType)
				.orElseGet(() -> createOAuthUser(oAuth2User, providerType));

		return new AuthUser(user, oAuth2User.getAttributes());
	}


	/**
	 * OAuth 유저로 서비스 유저 정보 저장
	 *
	 * @param oAuth2User   OAuth User 정보
	 * @param providerType OAuth Client 타입
	 * @return 생성한 유저
	 */
	public User createOAuthUser(
			OAuth2User oAuth2User,
			ProviderType providerType
	) {

		log.info("[OAuth 유저 생성]");

		// 이메일 중복 검증
		String email = oAuth2User.getAttribute("email");
		validateOAuthEmail(email);

		String name = oAuth2User.getAttribute("name");
		String password = RandomCodeUtil.generateUUID();

		return userRepository.save(User.builder()
				.name(name)
				.email(email)
				.password(password)
				.role(RoleType.USER)
				.providerType(providerType)
				.build());
	}

	/**
	 * OAuth 회원 생성 시 이메일 중복 여부 검증
	 *
	 * @param email 검증할 이메일
	 */
	private void validateOAuthEmail(String email) {

		log.info("[OAuth 이메일 중복 검증][{}]", email);

		if (userRepository.existsByEmail(email)) {
			log.info("[이미 가입된 이메일]");
			throw new ServiceException(ErrorType.OAUTH_EMAIL_CONFLICT);
		}
	}
}
