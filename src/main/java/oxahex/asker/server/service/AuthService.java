package oxahex.asker.server.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import oxahex.asker.server.cache.RedisRepository;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.domain.user.UserRepository;
import oxahex.asker.server.dto.JoinDto.JoinReqDto;
import oxahex.asker.server.dto.JoinDto.JoinResDto;
import oxahex.asker.server.dto.TokenDto;
import oxahex.asker.server.error.AuthException;
import oxahex.asker.server.error.ServiceException;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.type.ErrorType;
import oxahex.asker.server.type.JwtTokenType;
import oxahex.asker.server.type.RedisType;
import oxahex.asker.server.type.RoleType;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;
	private final RedisRepository redisRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.info("[이메일 유저 인증][email={}]", username);
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new AuthException(ErrorType.AUTHENTICATION_FAILURE));

		// TODO: 토큰 발급 여기에서

		return new AuthUser(user);
	}

	/**
	 * 유저 생성
	 * <p> 사용자 Name, Email, Password 를 받아 새로운 유저 생성
	 * <p> 검증: Email 중복 여부
	 *
	 * @param joinReqDto name, email, password
	 * @return 생성된 User Entity
	 */
	public JoinResDto createUser(JoinReqDto joinReqDto) {

		log.info("[유저 생성][email={}]", joinReqDto.getEmail());

		// 유저 생성 시 이메일 중복 검증
		validateEmail(joinReqDto.getEmail());

		User user = userRepository.save(User.builder()
				.name(joinReqDto.getName())
				.email(joinReqDto.getEmail())
				.password(passwordEncoder.encode(joinReqDto.getPassword()))
				.role(RoleType.USER)
				.build());

		return new JoinResDto(user);
	}

	/**
	 * JWT Access Token 재발급
	 *
	 * @param authUser     로그인 유저
	 * @param refreshToken Refresh Token
	 * @return Access Token
	 */
	public TokenDto reIssueAccessToken(
			AuthUser authUser,
			String refreshToken
	) {

		String userEmail = authUser.getUsername();
		String cachedToken = redisRepository.get(RedisType.REFRESH_TOKEN, userEmail);

		// 캐시되지 않은 경우 401 -> 다시 로그인
		if (cachedToken == null) {
			throw new ServiceException(ErrorType.TOKEN_EXPIRED);
		}

		// 서버 측에 저장된 토큰과 동일한지 확인
		if (!Objects.equals(refreshToken, cachedToken)) {
			throw new ServiceException(ErrorType.TOKEN_REISSUE_FAILURE);
		}

		// Access Token 재발급
		String reissuedToken = JwtTokenService.create(authUser, JwtTokenType.ACCESS_TOKEN);

		return new TokenDto(reissuedToken, cachedToken);
	}

	/**
	 * 이메일 중복 검증
	 *
	 * @param email 검증할 이메일
	 */
	private void validateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new ServiceException(ErrorType.EMAIL_CONFLICT);
		}
	}
}
