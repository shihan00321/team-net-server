package com.teamnet.team_net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class TeamNetApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamNetApplication.class, args);
	}

}
