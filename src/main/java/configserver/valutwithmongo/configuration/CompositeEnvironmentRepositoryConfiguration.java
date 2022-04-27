package configserver.valutwithmongo.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.environment.*;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Configuration
public class CompositeEnvironmentRepositoryConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectProvider<HttpServletRequest> request;

    @Autowired
    private EnvironmentWatch watch;

    private RestTemplate rest;

    @Autowired
    VaultEnvironmentProperties properties;

    @Bean
    @Primary
    public CompositeEnvironmentRepository compositeEnvironmentRepository() {
        MongoEnvironmentRepository mongoEnvironmentRepository = new MongoEnvironmentRepository(1, mongoTemplate);
        VaultEnvironmentRepository vaultEnvironmentRepository = new VaultEnvironmentRepository(request, watch, rest, properties);
        return new CompositeEnvironmentRepository(Arrays.asList(mongoEnvironmentRepository, vaultEnvironmentRepository), false);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
