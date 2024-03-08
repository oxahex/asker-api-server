package oxahex.asker.server.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import oxahex.asker.server.type.ProviderType;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	@Query("select u from User as u where u.email = :email and u.provider = :providerType")
	Optional<User> findOAuthUser(
			@Param("email") String email,
			@Param("providerType") ProviderType providerType
	);
}
