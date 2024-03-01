package oxahex.asker.server.domain.ask;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AskRepository extends JpaRepository<Ask, Long> {

	// DISPATCH 테이블 ask_id, answer_user_id가 일치하는 ASK 데이터만 조회
	@Query("select a from Ask a inner join Dispatch d ON a.id = d.ask.id where d.ask.id = :askId AND d.receiveUser.id = :userId")
	Optional<Ask> findDispatchedAsk(Long askId, Long userId);

	// DISPATCH 테이블 answer_user_id와 일치하는 ASK 데이터 조회
	@Query("select a from Ask as a join Dispatch as d on a.id = d.ask.id where d.receiveUser.id = :userId")
	Page<Ask> findAllDispatchedAsks(@Param("userId") Long userId, PageRequest pageRequest);

}
