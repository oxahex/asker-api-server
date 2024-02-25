package oxahex.asker.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.error.AuthException;
import oxahex.asker.server.security.AuthUser;
import oxahex.asker.server.type.ErrorType;
import oxahex.asker.server.type.JwtTokenType;
import oxahex.asker.server.type.RoleType;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private static final String HEADER = "Authorization";
	private static final String KEY_ID = "id";
	private static final String KEY_ROLE = "role";
	private static String JWT_TOKEN_KEY;

	/**
	 * JWT Token 생성, 발급
	 *
	 * @param authUser  Email, Password 인증 완료 된 UserDetails 객체
	 * @param tokenType 생성할 토큰 타입(ACCESS_TOKEN, REFRESH_TOKEN)
	 * @return JWT Token
	 */
	public static String create(AuthUser authUser, JwtTokenType tokenType) {

		log.info("[JWT {} 생성][{}]", tokenType.name(), authUser.getUsername());

		Date now = new Date(System.currentTimeMillis());
		Date expiredDate = new Date(now.getTime() + tokenType.getExpireTime());

		return JWT.create()
				.withSubject(authUser.getUsername())
				.withIssuedAt(now)
				.withExpiresAt(expiredDate)
				.withClaim(KEY_ID, authUser.getUser().getId())
				.withClaim(KEY_ROLE, authUser.getUser().getRole().name())
				.sign(Algorithm.HMAC256(JWT_TOKEN_KEY));
	}

	/**
	 * 토큰 검증
	 * <p>검증된 유저의 경우 강제 로그인 처리를 위해 UserDetails 객체를 만들어 반환
	 *
	 * @param token 검증할 토큰
	 * @return 검증된 유저의 UserDetails 객체
	 */
	public static AuthUser verify(String token) {

		log.info("[토큰 검증][{}]", token);

		DecodedJWT decodedJWT =
				JWT.require(Algorithm.HMAC256(JWT_TOKEN_KEY)).build().verify(token);

		// 만료된 토큰
		isExpiredToken(decodedJWT);

		Long id = decodedJWT.getClaim(KEY_ID).asLong();
		String email = decodedJWT.getSubject();
		String role = decodedJWT.getClaim(KEY_ROLE).asString();

		User user = User.builder()
				.id(id)
				.email(email)
				.role(RoleType.valueOf(role))
				.build();

		log.info("[토큰 검증 완료][id={}][email={}][role={}]", id, email, role);

		return new AuthUser(user);
	}

	private static void isExpiredToken(DecodedJWT token) {
		if (token.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
			throw new AuthException(ErrorType.TOKEN_EXPIRED);
		}
	}

	/**
	 * JWT_SECRET_KEY static 설정
	 * TODO: JWT 관련 데이터(Authorization Header, Secret Key, Bearer , Expire Time 등)를 관리하는 VO를 따로 만드는 것 고려
	 *
	 * @param key @Value 주입 받은 JWT SECRET KEY
	 */
	@Value("${spring.jwt.key}")
	private void setKey(String key) {
		JWT_TOKEN_KEY = key;
	}
}
