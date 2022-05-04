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

//    @Value("${spring.cloud.config.server.vault.token}")
//    private String vaultToken;
//
//    @Value("${spring.cloud.config.server.vault.token2}")
//    private String vaultToken2;

    SpringVaultEnvironmentRepository vaultRepoObjectToWrite;
    SpringVaultEnvironmentRepository vaultRepoObjectToRead;
    SpringVaultEnvironmentRepository newVaultReadRepo;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {

        //MongoRepository object to read properties from mongoDB
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(Ordered.LOWEST_PRECEDENCE, mongoTemplate);


        VaultEnvironmentProperties readProps = new VaultEnvironmentProperties();
        readProps.setAuthentication(VaultEnvironmentProperties.AuthenticationMethod.APPROLE);
        readProps.getAppRole().setRole("jenkins");
        readProps.getAppRole().setAppRolePath("approle/");

        readProps.getAppRole().setRoleId("e18c76f3-5d18-49d1-2d41-2dcbf9327249");
        readProps.getAppRole().setSecretId("602568f3-4c52-c35e-cc62-46a462b3d77a");
        readProps.setProfileSeparator("/");
        this.newVaultReadRepo = vaultEnvironmentRepositoryFactory
                .build(readProps);

        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, newVaultReadRepo),
                false);
    }


    private VaultEnvironmentProperties setVaultEnvironmentProperties(String profileSeparator, int kvVersion, String vaultToken) {
        VaultEnvironmentProperties properties = new VaultEnvironmentProperties();
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
