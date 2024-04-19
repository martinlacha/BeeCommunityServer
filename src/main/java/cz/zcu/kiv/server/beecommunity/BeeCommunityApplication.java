package cz.zcu.kiv.server.beecommunity;

import cz.zcu.kiv.server.beecommunity.config.PropertiesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main function and entry point where start spring application
 */

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfiguration.class)
public class BeeCommunityApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeeCommunityApplication.class, args);
	}

}
