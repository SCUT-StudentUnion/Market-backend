package org.scutsu.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class MarketApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		Locale.setDefault(Locale.CHINA);

		SpringApplication.run(MarketApplication.class, args);
	}

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
}
