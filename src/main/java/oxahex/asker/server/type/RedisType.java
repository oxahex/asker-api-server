package oxahex.asker.server.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisType {

	REFRESH_TOKEN("RTOKEN_", 1000 * 60 * 60 * 24L),
	EMAIL_CODE("ECODE_", 1000 * 60 * 10L);

	private final String prefix;
	private final Long expiredTime;
}
