package oxahex.asker.server.domain.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

	@Query("select a from Answer as a where a.answerUser.id = :userId")
	Page<Answer> findAllByUserId(@Param("userId") Long userId, PageRequest pageRequest);

}
