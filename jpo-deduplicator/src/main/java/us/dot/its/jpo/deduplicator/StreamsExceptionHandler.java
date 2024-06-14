package us.dot.its.jpo.deduplicator;

import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;



/**
 * Handler for unhandled exceptions thrown from Streams topologies that
 *  logs the exception to a topic, and allows choosing the shutdown behavior.
 * 
 * See {@link https://cwiki.apache.org/confluence/display/KAFKA/KIP-671%3A+Introduce+Kafka+Streams+Specific+Uncaught+Exception+Handler}
 * for a description of the options.
 */
public class StreamsExceptionHandler implements StreamsUncaughtExceptionHandler {

    final static Logger logger = LoggerFactory.getLogger(StreamsExceptionHandler.class);

    final KafkaTemplate<String, String> kafkaTemplate;
    final String topology;
    final String notificationTopic;
    
    @Autowired
    public StreamsExceptionHandler(KafkaTemplate<String, String> kafkaTemplate, String topology, String notificationTopic) {
            this.kafkaTemplate = kafkaTemplate;
            this.topology = topology;
            this.notificationTopic = notificationTopic;
    }
    
    @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {
        try {
            logger.error(String.format("Uncaught exception in stream topology %s", topology), exception);
        } catch (Exception ex) {
            logger.error("Exception sending kafka streams error event", ex);
        }

        
        // SHUTDOWN_CLIENT option shuts down quickly.
        return StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
        
        // SHUTDOWN_APPLICATION shuts down more slowly, but cleans up more thoroughly
        //return StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;

        // "Replace Thread" mode can be used to keep the streams client alive, 
        // however if the cause of the error was not transient, but due to a code error processing
        // a record, it can result in the record being repeatedly processed throwing the
        // same error
        //
        //return StreamThreadExceptionResponse.REPLACE_THREAD;
    }
    
}
