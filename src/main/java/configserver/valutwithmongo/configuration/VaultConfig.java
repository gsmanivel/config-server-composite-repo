package configserver.valutwithmongo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.environment.VaultEnvironmentProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class VaultConfig extends AbstractVaultConfiguration {


    @Override
    public VaultEndpoint vaultEndpoint() {
        URI vaultUri=null;
        try {
            vaultUri= new URI("http://127.0.0.1:8200");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return VaultEndpoint.from(vaultUri);
    }

    @Override
    public ClientAuthentication clientAuthentication() {

        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
                .appRole("configserver")
                .roleId(AppRoleAuthenticationOptions.RoleId.provided("ea0de26c-2aa2-e218-920a-5ef4fd883bdf"))
                .secretId(AppRoleAuthenticationOptions.SecretId.provided("0bb0f2cc-e97a-cfab-d708-cb38a00e25b4"))
                .path("gateway/dev")
                .build();
        return new AppRoleAuthentication(options, restOperations());
    }
}

