/**
 * 
 */
package de.evoila;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.bus.BusAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

/**
 * @author Johannes Hiemer.
 * Deploying to Space for testing purposes is as follows:
 * `cf create-service-broker name username password url --space-scoped`
 */
@RefreshScope
@SpringBootApplication
@EnableAutoConfiguration(exclude = { RabbitAutoConfiguration.class, BusAutoConfiguration.class })
public class Application {

	@Bean(name = "customProperties")
	public Map<String, String> customProperties() {
		Map<String, String> customProperties = new HashMap<String, String>();

		return customProperties;
	}

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class);
		springApplication.addListeners(new ApplicationPidFileWriter());
		ApplicationContext ctx = springApplication.run(args);

		Assert.notNull(ctx);
	}
}