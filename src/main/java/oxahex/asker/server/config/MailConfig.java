package oxahex.asker.server.config;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Slf4j
@Configuration
public class MailConfig {

	@Value("${spring.mail.host}")
	private String host;

	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.mail.port}")
	private int port;

	@Value("${spring.mail.password}")
	private String password;

	private static final String ENCODING = "UTF-8";

	@Bean
	public JavaMailSender javaMailSender() {

		log.debug("[Bean 등록] JavaMailSender");

		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);
		javaMailSender.setUsername(username);
		javaMailSender.setPort(port);
		javaMailSender.setPassword(password);
		javaMailSender.setDefaultEncoding(ENCODING);

		// TODO: property 주입 방법 변경 고려
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		javaMailSender.setJavaMailProperties(properties);

		return javaMailSender;
	}
}
