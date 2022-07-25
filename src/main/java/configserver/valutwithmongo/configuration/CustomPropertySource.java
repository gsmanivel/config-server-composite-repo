package configserver.valutwithmongo.configuration;

import java.util.LinkedHashMap;

public class CustomPropertySource {
    private String name;
    private String profile;
    private String label;
    private LinkedHashMap<String, Object> source = new LinkedHashMap<>();

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
