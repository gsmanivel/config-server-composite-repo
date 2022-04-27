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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MongoEnvironmentRepository implements EnvironmentRepository, Ordered {

    private final MongoTemplate mongoTemplate;
    private final MapFlattener mapFlattener;

    private int order = Ordered.LOWEST_PRECEDENCE;

    public MongoEnvironmentRepository(int order,MongoTemplate mongoTemplate) {
        this.mongoTemplate=mongoTemplate;
        this.mapFlattener= new MapFlattener();
        this.order = order;
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);

        Query query= new Query();
        query.addCriteria(Criteria.where("name").in(application));
        query.addCriteria(Criteria.where("profile").in(profiles));
        query.addCriteria(Criteria.where("label").in(label));
        Environment environment;

        try {
            List<MongoPropertySource> sources = mongoTemplate.find(query,MongoPropertySource.class,"gateway");
            environment = new Environment(application, profiles, label, null, null);
            for(MongoPropertySource source : sources){
                String sourceName = source.getProfile() + source.getName();
                Map<String,Object> flatSource = mapFlattener.flatten(source.getSource());
                PropertySource propertySource = new PropertySource(sourceName,flatSource);
                environment.add(propertySource);
            }
        }catch (Exception e) {
                throw new IllegalStateException("Can not load environment",e);
        }
        return environment;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public static class MongoPropertySource {
        private String name;
        private String profile;
        private String label;
        private LinkedHashMap<String,Object> source = new LinkedHashMap<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public LinkedHashMap<String, Object> getSource() {
            return source;
        }

        public void setSource(LinkedHashMap<String, Object> source) {
            this.source = source;
        }
    }


    private static class MapFlattener extends YamlProcessor {
        public Map<String,Object> flatten(Map<String,Object> source){
            return getFlattenedMap(source);
        }
    }
}
