package us.dot.its.jpo.conflictmonitor.monitor.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.domain.Sort;

@Configuration
@DependsOn("mongoTemplate") 
@Profile({"!test & !testConfig"})
public class MongoCollectionsConfig {
    private static final Logger logger = LoggerFactory.getLogger(MongoCollectionsConfig.class);

    @Autowired
    private MongoIndexCollection mongoIndexesListPojo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        for (MongoIndexPojo index : mongoIndexesListPojo.getIndexes()) {
            removeTTLIndex(index.getName(), index.getTimefield());
            createTTLIndex(index.getName(), index.getTimefield(), index.getExpiretime());
        }
    }

    public void removeTTLIndex(String collectionName, String field) {
        Collection<String> col = new ArrayList<String>();
        col.add(field);
        List<IndexInfo> indexes = mongoTemplate.indexOps(collectionName).getIndexInfo();
        for (IndexInfo index : indexes) {
            if (index.isIndexForFields(col)) {
                mongoTemplate.indexOps(collectionName).dropIndex(index.getName());
            }
        }
    }

    public void createTTLIndex(String collectionName, String field, Integer expireAfterSeconds) {
        mongoTemplate.indexOps(collectionName)
                     .ensureIndex(new Index().on(field, Sort.Direction.ASC)
                     .expire(expireAfterSeconds));
        logger.info("Created index " + collectionName + " on field " + field + " with expiry time " + String.valueOf(expireAfterSeconds));
    }
}