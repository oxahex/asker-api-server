package oxahex.asker.server.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import oxahex.asker.server.type.RedisType;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * Redis 데이터 삽입
	 *
	 * @param type  Prefix, ExpireTime
	 * @param key   Unique 키
	 * @param value 저장하려는 값
	 */
	public void save(RedisType type, String key, Object value) {
		redisTemplate.opsForValue().set(
				type.getPrefix() + key,
				value,
				type.getExpiredTime(),
				TimeUnit.SECONDS
		);
	}

	/**
	 * Redis 데이터 삭제
	 *
	 * @param key 삭제하고자 하는 값의 키
	 */
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * Redis 데이터 존재 여부 확인
	 *
	 * @param key 확인하려는 데이터의 키
	 * @return boolean
	 */
	public boolean isExists(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
}
