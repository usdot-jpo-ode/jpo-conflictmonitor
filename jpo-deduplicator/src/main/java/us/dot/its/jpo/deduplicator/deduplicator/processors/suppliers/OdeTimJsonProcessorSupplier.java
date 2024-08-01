package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import com.fasterxml.jackson.databind.JsonNode;

public class OdeTimJsonProcessorSupplier implements ProcessorSupplier<String, JsonNode, String, JsonNode> {
    
    String storeName;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public OdeTimJsonProcessorSupplier(String storeName){
        this.storeName = storeName;
    }

    @Override
    public Processor<String, JsonNode, String, JsonNode> get() {

        System.out.println("Creating new Processor");
        return new Processor<String, JsonNode, String, JsonNode>() {
            
            private ProcessorContext<String, JsonNode> context;
            private KeyValueStore<String, JsonNode> store;

            
            @Override
            public void init(ProcessorContext<String, JsonNode> context) {
                System.out.println("Initializing new Processor");
                this.context = context;
                store = context.getStateStore(storeName);
                this.context.schedule(Duration.ofSeconds(120), PunctuationType.WALL_CLOCK_TIME, this::cleanupOldKeys);
            }

            @Override
            public void process(Record<String, JsonNode> record) {

                // Don't do anything if key is bad
                if(record.key().equals("")){
                    return;
                }

                JsonNode lastRecord = store.get(record.key());
                if(lastRecord == null || lastRecord.get("metadata") == null){
                    store.put(record.key(), record.value());
                    context.forward(record);
                }

        
                Instant oldValueTime = getInstantFromJsonTim(lastRecord);
                Instant newValueTime = getInstantFromJsonTim(record.value());
        
                if(newValueTime.minus(Duration.ofMinutes(1)).isAfter(oldValueTime)){
                    store.put(record.key(), record.value());
                    context.forward(record);
                }
            }

            public Instant getInstantFromJsonTim(JsonNode tim) {
                
                try {
                    String time = tim.get("metadata").get("odeReceivedAt").asText();
                    return Instant.from(formatter.parse(time));
                } catch (Exception e) {
                    System.out.println("Failed to parse time" + tim);
                    return Instant.ofEpochMilli(0);
                }
            }

            private void cleanupOldKeys(final long timestamp) {
                try (KeyValueIterator<String, JsonNode> iterator = store.all()) {
                    while (iterator.hasNext()) {
                    
                    KeyValue<String, JsonNode> record = iterator.next();
                        // If the record is more than 30 seconds old
                        if(Instant.ofEpochMilli(timestamp).minusSeconds(30).isAfter(getInstantFromJsonTim(record.value))){
                            store.delete(record.key);
                        }
                    }
                }
            }

        };
    }
}