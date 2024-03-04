package oxahex.asker.server.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

	private final JavaMailSender javaMailSender;
	private final static String ENCODING = "UTF-8";

	public void sendEmail(String email, String subject, String contents) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, ENCODING);
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(contents);

			javaMailSender.send(mimeMessage);
			log.info("[메일 전송 완료]");

		} catch (MessagingException e) {

			log.error("[메일 전송 실패]");
			throw new RuntimeException(e);
		}
	}
}
