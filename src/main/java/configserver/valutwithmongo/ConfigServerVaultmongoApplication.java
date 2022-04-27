package configserver.valutwithmongo;

import configserver.valutwithmongo.configuration.EnableCompositeConfigServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCompositeConfigServer
public class ConfigServerVaultmongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerVaultmongoApplication.class, args);
	}

}
