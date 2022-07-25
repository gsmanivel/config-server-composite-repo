package configserver.valutwithmongo.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.VaultEnvironmentProperties;
import org.springframework.cloud.config.server.environment.vault.SpringVaultEnvironmentRepository;
import org.springframework.cloud.config.server.environment.vault.SpringVaultEnvironmentRepositoryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
public class CompositeEnvironmentRepositoryConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {

        //MongoRepository
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(Ordered.LOWEST_PRECEDENCE, mongoTemplate);

        //VaultRepository
        VaultEnvironmentRepository vaultEnvironmentRepository = new VaultEnvironmentRepository(Ordered.HIGHEST_PRECEDENCE);
        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, vaultEnvironmentRepository),
                false);
    }

}
