package oxahex.asker.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.domain.user.UserRepository;
import oxahex.asker.server.error.AuthException;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.type.ErrorType;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.info("[이메일 유저 인증][email={}]", username);
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new AuthException(ErrorType.AUTHENTICATION_FAILURE));

		return new AuthUser(user);
	}
}
