package configserver.valutwithmongo.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import configserver.valutwithmongo.configuration.EnvironmentUtils;


public class VaultEnvironmentRepository implements EnvironmentRepository, Ordered {
    private final EnvironmentUtils.MapFlattener mapFlattener;
    private final int order;
    private final EnvironmentUtils environmentUtils;

    public VaultEnvironmentRepository(int order) {
        this.mapFlattener = new EnvironmentUtils.MapFlattener();
        this.order = order;
        environmentUtils = new EnvironmentUtils();
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        //Split the String using comma separator and convert it as List of String
        List<String> applicationList = environmentUtils.getList(application);
        String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);
        Collections.reverse(applicationList);
        List<String> profileList = environmentUtils.getList(profile);
        List<String> labelList = environmentUtils.getList(label);

        //Loop through Application , profile and label and create environment for each combination
        Environment environment = new Environment(application, profiles, label, null, null);
        List<CustomPropertySource> sources = new ArrayList<>();
        CustomPropertySource vaultPropertySource;
        try {
            for (String strApplication : applicationList) {
                for (String strProfile : profileList) {
                    vaultPropertySource = environmentUtils.getSecret(strApplication, strProfile, label);
                    if (!(vaultPropertySource == null)) {
                        sources.add(vaultPropertySource);
                        environmentUtils.sortSourceByProfile(sources, profileList);
                        environmentUtils.sortSourceByLabel(sources,labelList);
                    }
                }
            }
            //create Data Source to return to consumer
            for (CustomPropertySource source : sources) {
                String sourceName = "vault:" + source.getName() + "/" + source.getProfile();
                Map<String, Object> flatSource = mapFlattener.flatten(source.getSource());
                PropertySource propertySource = new PropertySource(sourceName, flatSource);
                environment.add(propertySource);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Can not load environment", e);
        }
        return environment;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
