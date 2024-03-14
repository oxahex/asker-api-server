package oxahex.asker.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HealthCheckController {

	@GetMapping
	public String healthCheck() {
		return "Asker Server is Up and Running...";
	}

}
