package configserver.valutwithmongo.configuration;

import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(CompositeEnvironmentRepositoryConfiguration.class)
@EnableConfigServer
public @interface EnableCompositeConfigServer {
}
