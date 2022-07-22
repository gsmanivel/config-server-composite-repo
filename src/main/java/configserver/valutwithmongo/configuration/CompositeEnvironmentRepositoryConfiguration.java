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
    VaultEnvironmentProperties vaultEnvironmentProperties;

    @Autowired
    private SpringVaultEnvironmentRepositoryFactory vaultEnvironmentRepositoryFactory;
    SpringVaultEnvironmentRepository vaultEnvironmentRepositoryUsingAppRole;
    SpringVaultEnvironmentRepository vaultEnvironmentRepositoryUsingToken;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {

        //MongoRepository object to read properties from mongoDB
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(Ordered.LOWEST_PRECEDENCE, mongoTemplate);

        //Below section is to enable vault environment with Token
        //*************************************************************************************************************
      /*  VaultEnvironmentProperties vaultEnvironmentPropertiesForToken = new VaultEnvironmentProperties();
//        String path = "secret/myapp";
//        properties.setPathToKey(path);
        vaultEnvironmentPropertiesForToken.setProfileSeparator("/");
        vaultEnvironmentPropertiesForToken.setKvVersion(2);
        vaultEnvironmentPropertiesForToken.setToken("hvs.ASUinDQHL7TVJSIoiO8ISAbS");
        vaultEnvironmentPropertiesForToken.setOrder(Ordered.HIGHEST_PRECEDENCE);
        vaultEnvironmentRepositoryUsingToken = vaultEnvironmentRepositoryFactory.build(vaultEnvironmentPropertiesForToken);
        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, vaultEnvironmentRepositoryUsingToken),
                false);*/
        //*************************************************************************************************************

        //Below section is to enable vault environment with Approle Auth
        //*************************************************************************************************************
       // VaultEnvironmentProperties vaultEnvironmentPropertiesForAppRole = new VaultEnvironmentProperties();
        System.out.println("Configserver:::"+ vaultEnvironmentProperties.getAppRole().getRole());
//        vaultEnvironmentPropertiesForAppRole.setAuthentication(VaultEnvironmentProperties.AuthenticationMethod.APPROLE);
//        vaultEnvironmentPropertiesForAppRole.getAppRole().setRole("configserver");
//        vaultEnvironmentPropertiesForAppRole.getAppRole().setAppRolePath("secret/gateway/dev");
        //vaultEnvironmentPropertiesForAppRole.setProfileSeparator("/");

//        vaultEnvironmentPropertiesForAppRole.getAppRole().setRoleId("ea0de26c-2aa2-e218-920a-5ef4fd883bdf");
//        vaultEnvironmentPropertiesForAppRole.getAppRole().setSecretId("0bb0f2cc-e97a-cfab-d708-cb38a00e25b4");

        this.vaultEnvironmentRepositoryUsingAppRole = vaultEnvironmentRepositoryFactory
                .build(vaultEnvironmentProperties);
        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository,vaultEnvironmentRepositoryUsingAppRole),
                false);

        //*************************************************************************************************************
    }

}
