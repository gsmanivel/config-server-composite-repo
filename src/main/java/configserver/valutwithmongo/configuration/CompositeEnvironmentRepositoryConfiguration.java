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
//        readProps.setAuthentication(VaultEnvironmentProperties.AuthenticationMethod.APPROLE);
//        readProps.getAppRole().setRole("myapp-read");
//        readProps.getAppRole().setAppRolePath("new-app/dev");
//        readProps.getAppRole().setRoleId("ac7fdff3-b3b8-b19f-86bd-005a486640b3");
//        readProps.getAppRole().setSecretId("7dacff87-be0a-4c9a-2da3-754b024f410a");
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
