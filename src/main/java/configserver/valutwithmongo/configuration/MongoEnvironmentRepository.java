package configserver.valutwithmongo.configuration;

import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.*;


public class MongoEnvironmentRepository implements EnvironmentRepository, Ordered {

    private final MongoTemplate mongoTemplate;
    private final EnvironmentUtils.MapFlattener mapFlattener;
    private final int order;
    private final EnvironmentUtils environmentUtils;

    public MongoEnvironmentRepository(int order,MongoTemplate mongoTemplate) {
        this.mongoTemplate=mongoTemplate;
        this.mapFlattener = new EnvironmentUtils.MapFlattener();
        this.order = order;
        environmentUtils = new EnvironmentUtils();
    }

    @Override
    public Environment findOne(String application, String profile, String label) {

        //Split the String using comma separator and convert it as List of String
        List<String> applicationList = environmentUtils.getList(application);
        Collections.reverse(applicationList);
        String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);
        List<String> profileList = environmentUtils.getList(profile);
        List<String> labelList = environmentUtils.getList(label);

        Query query= new Query();
        query.addCriteria(Criteria.where("name").in(applicationList.toArray()));
        query.addCriteria(Criteria.where("profile").in(profileList.toArray()));
        query.addCriteria(Criteria.where("label").in(labelList.toArray()));
        Environment environment= new Environment(application, profiles, label, null, null);;
        List<CustomPropertySource> sources;
        for(String strApplication :applicationList){
            try {
                sources = mongoTemplate.find(query, CustomPropertySource.class, strApplication);
                environmentUtils.sortSourceByLabel(sources,labelList);
                environmentUtils.sortSourceByProfile(sources,profileList);
                for (CustomPropertySource source : sources) {
                    String sourceName = "mongodb:"+  source.getName()+"/"+source.getProfile() ;
                    Map<String, Object> flatSource = mapFlattener.flatten(source.getSource());
                    PropertySource propertySource = new PropertySource(sourceName, flatSource);
                    environment.add(propertySource);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Can not load environment", e);
            }
        }
        return environment;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
