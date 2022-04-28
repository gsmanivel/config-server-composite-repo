package configserver.valutwithmongo.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.environment.*;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.vault.SpringVaultClientConfiguration;
import org.springframework.cloud.config.server.environment.vault.SpringVaultEnvironmentRepository;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Configuration
public class CompositeEnvironmentRepositoryConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectProvider<HttpServletRequest> request;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(1, mongoTemplate);

        VaultEnvironmentProperties properties = new VaultEnvironmentProperties();
        String path = "secret/myapp";
        properties.setPathToKey(path);
        properties.setKvVersion(2);

        //Remove this - hard code value after POC
        VaultTemplate vaultTemplate = new VaultTemplate(VaultEndpoint.create("localhost", 8200),
                new TokenAuthentication("hvs.NtkFozm8Zffl8TBQVQ206dUp"));


        //Remove this - hard code value after POC
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue(path, KeyValueBackend.KV_2);

        SpringVaultEnvironmentRepository vaultEnvironmentRepository =
                new SpringVaultEnvironmentRepository(request, new EnvironmentWatch.Default(), properties, vaultKeyValueOperations);
        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, vaultEnvironmentRepository), false);
    }


    @Bean
    public ObjectProvider<HttpServletRequest> getObjectProvider() {
        return new ObjectProvider<HttpServletRequest>() {
            @Override
            public HttpServletRequest getObject(Object... objects) throws BeansException {
                return null;
            }

            @Override
            public HttpServletRequest getIfAvailable() throws BeansException {
                return null;
            }

            @Override
            public HttpServletRequest getIfUnique() throws BeansException {
                return null;
            }

            @Override
            public HttpServletRequest getObject() throws BeansException {
                return null;
            }
        };
    }
}
