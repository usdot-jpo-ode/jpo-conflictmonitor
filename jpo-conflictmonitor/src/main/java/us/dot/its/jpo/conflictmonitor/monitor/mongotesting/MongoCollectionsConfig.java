package us.dot.its.jpo.conflictmonitor.monitor.mongotesting;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;

@Configuration
@DependsOn("mongoTemplate")
public class MongoCollectionsConfig {
    private static final Logger logger = LoggerFactory.getLogger(MongoCollectionsConfig.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        
        logger.info(mongoTemplate.indexOps("CmIntersectionReferenceAlignmentEvents").getIndexInfo().toString()); // collection name string or .class
        //     .ensureIndex(
        //         new Index().on("eventGeneratedAt", Sort.Direction.ASC).expire(60)
        // );
    }
}