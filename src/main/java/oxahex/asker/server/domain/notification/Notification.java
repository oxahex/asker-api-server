package oxahex.asker.server.domain.notification;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import oxahex.asker.server.domain.user.User;
import oxahex.asker.server.type.NotificationType;


@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receive_user_id")
	private User receiveUser;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type")
	private NotificationType notificationType;

	@Type(JsonType.class)
	@Column(name = "front_matter", columnDefinition = "json", nullable = false)
	private NotificationFrontMatter frontMatter;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public Notification(
			Long id,
			User receiveUser,
			NotificationType notificationType,
			Long originId,
			Long originUserId,
			String excerpt
	) {
		this.id = id;
		this.receiveUser = receiveUser;
		this.notificationType = notificationType;
		this.frontMatter = NotificationFrontMatter.builder()
				.originId(originId)
				.originUserId(originUserId)
				.excerpt(excerpt).build();
	}

	public void read() {
		this.readAt = LocalDateTime.now();
	}
}
