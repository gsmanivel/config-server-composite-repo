package configserver.valutwithmongo.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.VaultEnvironmentProperties;
import org.springframework.cloud.config.server.environment.vault.SpringVaultEnvironmentRepository;
import org.springframework.cloud.config.server.environment.vault.SpringVaultEnvironmentRepositoryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class CompositeEnvironmentRepositoryConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SpringVaultEnvironmentRepositoryFactory vaultEnvironmentRepositoryFactory;

    @Value("${spring.cloud.config.server.vault.token}")
    private String vaultToken;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(1, mongoTemplate);

        VaultEnvironmentProperties properties = new VaultEnvironmentProperties();
        String path = "secret/myapp";
        properties.setPathToKey(path);
        properties.setKvVersion(2);
        properties.setToken(vaultToken); 
        
        SpringVaultEnvironmentRepository vaultEnvironmentRepository = vaultEnvironmentRepositoryFactory
                .build(properties);

        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, vaultEnvironmentRepository),
                false);
    }

}
