package com.dyma.tennis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dyma.tennis.shared.health.HealthCheckRepository;

import com.dyma.tennis.shared.health.HealthCheckController;

@SpringBootTest
class TennisApplicationTests {

	@Autowired
	private HealthCheckController healthCheckController;



	@Autowired
	private HealthCheckRepository healthCheckRepository;

	@Test
	void contextLoads() {
		Assertions.assertThat(healthCheckController).isNotNull();

		Assertions.assertThat(healthCheckRepository).isNotNull();
	}

}
