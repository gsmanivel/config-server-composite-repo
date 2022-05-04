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
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.vault.core.VaultKeyValueOperations;

@Configuration
public class CompositeEnvironmentRepositoryConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SpringVaultEnvironmentRepositoryFactory vaultEnvironmentRepositoryFactory;

    @Value("${spring.cloud.config.server.vault.token}")
    private String vaultToken;

    @Value("${spring.cloud.config.server.vault.token2}")
    private String vaultToken2;

    SpringVaultEnvironmentRepository vaultRepoObjectToWrite;
    SpringVaultEnvironmentRepository vaultRepoObjectToRead;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {

        //MongoRepository object to read properties from mongoDB
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(Ordered.LOWEST_PRECEDENCE, mongoTemplate);

        //VaultRepository object to read data from Vault
        this.vaultRepoObjectToRead = vaultEnvironmentRepositoryFactory
                .build(setVaultEnvironmentProperties("/", 2, vaultToken));

        vaultRepoObjectToRead.setOrder(Ordered.HIGHEST_PRECEDENCE);

        //VaultRepository object to write data from Vault
        /*this.vaultRepoObjectToWrite = vaultEnvironmentRepositoryFactory
                .build(setVaultEnvironmentProperties("/", 2, vaultToken2));

        vaultRepoObjectToWrite.setOrder(Ordered.HIGHEST_PRECEDENCE);*/


        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, vaultRepoObjectToRead),
                false);
    }


    private VaultEnvironmentProperties setVaultEnvironmentProperties(String profileSeparator, int kvVersion, String vaultToken) {
        VaultEnvironmentProperties properties = new VaultEnvironmentProperties();
//        String path = "secret/myapp";
//        properties.setPathToKey(path);
        properties.setProfileSeparator(profileSeparator);
        properties.setKvVersion(kvVersion);
        properties.setToken(vaultToken);
        return properties;
    }

    public SpringVaultEnvironmentRepository getVaultRepoObjectToWrite() {
        return vaultRepoObjectToWrite;
    }


    public SpringVaultEnvironmentRepository getVaultRepoObjectToRead() {
        return vaultRepoObjectToRead;
    }

}
