package oxahex.asker.server.domain.dispatch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oxahex.asker.server.domain.ask.Ask;
import oxahex.asker.server.domain.user.User;

@Entity
@Table(name = "dispatch")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dispatch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dispatch_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ask_id", nullable = false)
	private Ask ask;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receive_user_id")
	private User receiveUser;

	@Builder
	public Dispatch(Long id, Ask ask, User receiveUser) {
		this.id = id;
		this.ask = ask;
		this.receiveUser = receiveUser;
	}
}
