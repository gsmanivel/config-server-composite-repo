package configserver.valutwithmongo.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.*;

public class EnvironmentUtils {

    public String getToken() {
        String token = null;
        System.out.println("Fetching Token");
        try {
            WebClient webClient = WebClient.create("http://127.0.0.1:8200");
            MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();

            bodyValues.add("role_id", "702ca06f-8a31-6040-8042-12a65c2740eb");
            bodyValues.add("secret_id", "1d774d5b-19a7-bfb5-3edf-54c8360258f3");

            byte[] response = webClient.post()
                    .uri("/v1/auth/approle/login")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromFormData(bodyValues))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            if(!(response==null)) {
                JSONObject jsonResponse = new JSONObject(new String(response));
                token = jsonResponse.getJSONObject("auth").get("client_token").toString();
            }
        } catch (WebClientException we) {
            System.out.println("Error in Fetching vault token");
        }
        return token;
    }

    public CustomPropertySource getSecret(String appName, String profile, String label) {
        String finalResponse = null;
        String token = getToken();
        CustomPropertySource propertySource = null;
        try {
            WebClient webClient = WebClient.create("http://127.0.0.1:8200");

            byte[] response = webClient.get()
                    .uri("/v1/secret/data/" + appName + "/" + profile)
                    .header("X-Vault-Token", token)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            if(!(response==null)) {
                JSONObject jsonResponse = new JSONObject(new String(response));
                JSONObject finalJsonObj = jsonResponse.getJSONObject("data").getJSONObject("data");
                if (!finalJsonObj.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    LinkedHashMap<String, Object> dataSource = objectMapper.readValue(finalJsonObj.toString(), LinkedHashMap.class);
                    propertySource = new CustomPropertySource();
                    propertySource.setName(appName);
                    propertySource.setLabel(label);
                    propertySource.setProfile(profile);
                    propertySource.setSource(dataSource);
                }
            }
            return propertySource;
        } catch (WebClientException we) {
            System.out.println("Error in Fetching vault token");
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return propertySource;
    }

    void sortSourceByProfile(List<CustomPropertySource> sources, final List<String> profiles) {
        Collections.sort(sources, (o1, o2) -> {
            int i1 = profiles.indexOf(o1.getProfile());
            int i2 = profiles.indexOf(o2.getProfile());
            return Integer.compare(i2, i1);
        });
    }

    void sortSourceByLabel(List<CustomPropertySource> sources, final List<String> labels){
        Collections.sort(sources, (o1, o2) -> {
            int i1 = labels.indexOf(o1.getLabel());
            int i2 = labels.indexOf(o2.getLabel());
            return Integer.compare(i2, i1);
        });
    }


    List<String> getList( String strValue){
        List<String> dataList = null;
        if(strValue!=null && !strValue.isEmpty()) {
            String[] values = StringUtils.commaDelimitedListToStringArray(strValue);
            dataList = new ArrayList<>(Arrays.asList(values));
            dataList = new ArrayList<>(new LinkedHashSet<>(dataList));
        }
        return dataList;
    }

    static class MapFlattener extends YamlProcessor {
        public Map<String, Object> flatten(Map<String, Object> source) {
            return getFlattenedMap(source);
        }
    }
}
