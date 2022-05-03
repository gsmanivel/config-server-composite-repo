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
    private final MapFlattener mapFlattener;
    private int order;

    public MongoEnvironmentRepository(int order,MongoTemplate mongoTemplate) {
        this.mongoTemplate=mongoTemplate;
        this.mapFlattener= new MapFlattener();
        this.order = order;
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        String[] applications = StringUtils.commaDelimitedListToStringArray(application);
        String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);
        Collections.reverse(Arrays.asList(applications));
        Query query= new Query();
        query.addCriteria(Criteria.where("name").in(applications));
        query.addCriteria(Criteria.where("profile").in(profiles));
        query.addCriteria(Criteria.where("label").in(label));
        Environment environment= new Environment(application, profiles, label, null, null);;

        for(int i=0;i< applications.length; i++){
            try {
                List<MongoPropertySource> sources = mongoTemplate.find(query, MongoPropertySource.class, applications[i]);
                sortSourceByProfile(sources,Arrays.asList(profiles));
                for (MongoPropertySource source : sources) {
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

    private void sortSourceByLabel(List<MongoPropertySource> sources, final List<String> labels){
        Collections.sort(sources, new Comparator<MongoPropertySource>() {
            @Override
            public int compare(MongoPropertySource o1, MongoPropertySource o2) {
               int i1=labels.indexOf(o1.getLabel());
               int i2=labels.indexOf(o2.getLabel());
               return Integer.compare(i2,i1);
            }
        });
    }

    private void sortSourceByProfile(List<MongoPropertySource> sources, final List<String> profiles){
        Collections.sort(sources, new Comparator<MongoPropertySource>() {
            @Override
            public int compare(MongoPropertySource o1, MongoPropertySource o2) {
                int i1=profiles.indexOf(o1.getLabel());
                int i2=profiles.indexOf(o2.getLabel());
                return Integer.compare(i2,i1);
            }
        });
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
