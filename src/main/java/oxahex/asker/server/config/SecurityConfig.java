package oxahex.asker.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import oxahex.asker.server.cache.RedisRepository;
import oxahex.asker.server.error.handler.AuthenticationExceptionHandler;
import oxahex.asker.server.error.handler.AuthorizationExceptionHandler;
import oxahex.asker.server.security.AuthenticationFilter;
import oxahex.asker.server.security.AuthorizationFilter;
import oxahex.asker.server.security.OAuthFailureHandler;
import oxahex.asker.server.security.OAuthSuccessHandler;
import oxahex.asker.server.service.AuthService;
import oxahex.asker.server.service.OAuthService;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String LOGIN_PATH = "/api/auth/login";
	private final PasswordEncoder passwordEncoder;
	private final ObjectMapper objectMapper;
	private final AuthService authService;
	private final OAuthService oAuthService;
	private final RedisRepository redisRepository;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				.headers(HeadersConfigurer::disable)  // iframe 비허용
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(configurationSource()))  // CORS
				.httpBasic(AbstractHttpConfigurer::disable)  // 브라우저 팝업 인증 비허용
				.formLogin(AbstractHttpConfigurer::disable)  // HTTP API 기반 서버이므로 기본 Form Login 비허용
				.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
						SessionCreationPolicy.STATELESS));  // JWT 기반 인가 사용을 위해 Stateless로 설정

		// Path Level 진입 제안
		http
				.authorizeHttpRequests(request -> {
					request.requestMatchers("/").permitAll();        // for EB Health Check
					request.requestMatchers("/api/**").permitAll();
					request.anyRequest().authenticated();
				});

		// 인증, 인가 처리 필터 등록
		http.addFilter(authenticationFilter());  // 이메일 패스워드 기반 인증 처리
		http.addFilterBefore(authorizationFilter(), AuthenticationFilter.class);  // JWT 토큰 기반 인가 처리

		// OAuth
		http.oauth2Login(configure -> {
			configure.userInfoEndpoint(endPoint -> endPoint.userService(oAuthService));
			configure.successHandler(oAuthSuccessHandler());
			configure.failureHandler(oAuthFailureHandler());
		});

		// Security Filter 내 기본 예외 처리
		http
				.exceptionHandling(exceptionHandler -> {
					exceptionHandler.authenticationEntryPoint(
							new AuthenticationExceptionHandler(objectMapper));  // 인증 실패(401)
					exceptionHandler.accessDeniedHandler(
							new AuthorizationExceptionHandler(objectMapper));    // 인가 실패(403)
				});

		return http.build();
	}


	public CorsConfigurationSource configurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");  // GET, POST, PUT, DELETE JavaScript 요청 모두 허용
		configuration.addAllowedOriginPattern("*");  // 모든 주소 허용 -> 이후 변경(프론트쪽 Origin만 허용하도록)
		configuration.setAllowCredentials(true);  // 클라이언트 쿠키 요청 허용

		// 모든 주소 요청에 위 설정 적용
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("**", configuration);

		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		log.info("[AuthenticationManager 빈 등록]");
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(authService);
		provider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(provider);
	}

	@Bean
	public AuthenticationFilter authenticationFilter() {
		AuthenticationFilter filter = new AuthenticationFilter(objectMapper, redisRepository);
		filter.setFilterProcessesUrl(LOGIN_PATH);
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}

	@Bean
	public AuthorizationFilter authorizationFilter() {
		return new AuthorizationFilter(objectMapper);
	}

	@Bean
	AuthenticationSuccessHandler oAuthSuccessHandler() {
		return new OAuthSuccessHandler(redisRepository, objectMapper);
	}

	@Bean
	AuthenticationFailureHandler oAuthFailureHandler() {
		return new OAuthFailureHandler(objectMapper);
	}
}
