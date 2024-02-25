package oxahex.asker.server.domain.ask;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AskRepository extends JpaRepository<Ask, Long> {

	@Query("select a from Ask a inner join Dispatch d ON a.id = d.ask.id where d.ask.id = :askId AND d.answerUser.id = :userId")
	Optional<Ask> findDispatchedAsk(Long askId, Long userId);

}
