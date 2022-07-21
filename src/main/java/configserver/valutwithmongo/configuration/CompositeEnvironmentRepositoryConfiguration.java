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
    SpringVaultEnvironmentRepository vaultEnvironmentRepositoryUsingAppRole;
    SpringVaultEnvironmentRepository vaultEnvironmentRepositoryUsingToken;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {

        //MongoRepository object to read properties from mongoDB
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(Ordered.LOWEST_PRECEDENCE, mongoTemplate);

        //VaultRepository object to read data from Vault
        VaultEnvironmentProperties vaultEnvironmentPropertiesForToken = new VaultEnvironmentProperties();
//        String path = "secret/myapp";
//        properties.setPathToKey(path);
        vaultEnvironmentPropertiesForToken.setProfileSeparator("/");
        vaultEnvironmentPropertiesForToken.setKvVersion(2);
        vaultEnvironmentPropertiesForToken.setToken("hvs.ASUinDQHL7TVJSIoiO8ISAbS");
        vaultEnvironmentPropertiesForToken.setOrder(Ordered.HIGHEST_PRECEDENCE);
        vaultEnvironmentRepositoryUsingToken = vaultEnvironmentRepositoryFactory.build(vaultEnvironmentPropertiesForToken);
        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, vaultEnvironmentRepositoryUsingToken),
                false);


        //Below section is to enable vault environment with Approle Auth
        //*************************************************************************************************************
//        VaultEnvironmentProperties vaultEnvironmentPropertiesForAppRole = new VaultEnvironmentProperties();
//        vaultEnvironmentPropertiesForAppRole.setAuthentication(VaultEnvironmentProperties.AuthenticationMethod.APPROLE);
//        vaultEnvironmentPropertiesForAppRole.getAppRole().setRole("demorole");
//        //vaultEnvironmentProperties.getAppRole().setAppRolePath("gateway/dev");
//        //vaultEnvironmentPropertiesForAppRole.setProfileSeparator("/");
//
//        vaultEnvironmentPropertiesForAppRole.getAppRole().setRoleId("17cb3448-00cf-ad03-2cc9-4c944f2c1d40");
//        vaultEnvironmentPropertiesForAppRole.getAppRole().setSecretId("6c53898b-53d1-b8c0-5d1b-e433a2c54aa0");
//
//        this.vaultEnvironmentRepositoryUsingAppRole = vaultEnvironmentRepositoryFactory
//                .build(vaultEnvironmentPropertiesForAppRole);
//
//        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository,vaultEnvironmentRepositoryUsingAppRole),
//                false);

        //*************************************************************************************************************
    }

}
