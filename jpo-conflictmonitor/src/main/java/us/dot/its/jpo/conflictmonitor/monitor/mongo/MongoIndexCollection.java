package us.dot.its.jpo.conflictmonitor.monitor.mongo;

import java.util.Collection;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mongo")
public class MongoIndexCollection {
    private Collection<MongoIndexPojo> indexes;

    public Collection<MongoIndexPojo> getIndexes() {
        return this.indexes;
    }

    public void setIndexes(Collection<MongoIndexPojo> mongoIndexes) {
        this.indexes = mongoIndexes;
    }

    @Override
    public String toString() {
        return "{" +
            " mongoIndexes='" + getIndexes() + "'" +
            "}";
    }

}
