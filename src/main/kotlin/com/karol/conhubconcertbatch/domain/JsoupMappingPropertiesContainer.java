package com.karol.conhubconcertbatch.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import com.karol.conhubconcertbatch.domain.MappingPropertiesKt;
import java.util.List;

@ConfigurationProperties(prefix = "jsoup")
public class JsoupMappingPropertiesContainer {
    private List<MappingPropertiesKt> mappingPropertiesList;

    public List<MappingPropertiesKt> getMappingPropertiesList() {
        return mappingPropertiesList;
    }

    public void setMappingPropertiesList(List<MappingPropertiesKt> mappingPropertiesList) {
        this.mappingPropertiesList = mappingPropertiesList;
    }

    @Override
    public String toString() {
        return "JsoupMappingPropertiesContainer{" +
                "mappingPropertiesList=" + mappingPropertiesList +
                '}';
    }
}
