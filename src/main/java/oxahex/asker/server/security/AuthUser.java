package oxahex.asker.server.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import oxahex.asker.server.domain.user.User;

@Getter
@RequiredArgsConstructor
public class AuthUser implements UserDetails, OAuth2User {

	private User user;
	private Map<String, Object> attributes;

	/**
	 * Email User 생성
	 *
	 * @param user 저장된 유저
	 */
	public AuthUser(User user) {
		this.user = user;
	}

	/**
	 * OAuth User 생성
	 *
	 * @param user       저장된 유저
	 * @param attributes OAuth Attributes
	 */
	public AuthUser(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return this.user.getName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(this.user.getRole().name()));
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
