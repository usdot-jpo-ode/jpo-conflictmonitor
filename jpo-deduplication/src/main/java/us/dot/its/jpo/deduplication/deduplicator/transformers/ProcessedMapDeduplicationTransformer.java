// package us.dot.its.jpo.deduplication.deduplicator.transformers;
// import org.apache.kafka.streams.*;
// import org.apache.kafka.streams.kstream.*;
// import org.apache.kafka.streams.processor.ProcessorContext;
// import org.apache.kafka.streams.state.*;

// import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
// import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

// import java.time.Duration;
// import java.time.Instant;
// import java.util.Properties;



// public class ProcessedMapDeduplicationTransformer implements Transformer<String, ProcessedMap<LineString>, KeyValue<String, ProcessedMap<LineString>>> {
//     private KeyValueStore<String, ProcessedMap> mapStore;
//     private KeyValueStore<String, Instant> timeStore;
//     private Duration windowDuration;
//     ProcessorContext context;

//     public ProcessedMapDeduplicationTransformer(Duration windowDuration) {
//         this.windowDuration = windowDuration;
//     }

//     @SuppressWarnings("unchecked")
//     @Override
//     public void init(ProcessorContext context) {
//         mapStore = (KeyValueStore<String, ProcessedMap>) context.getStateStore("Processed");
//         this.context = context;
//     }

//     @Override
//     public KeyValue<String, ProcessedMap<LineString>> transform(String key, ProcessedMap<LineString> value) {
//         ProcessedMap lastMapSent = mapStore.get(key);
//         Instant currentTimestamp = Instant.ofEpochMilli(this.context.timestamp());
//         Instant lastTimestamp = timeStore.get(key);

//         if(lastMapSent == null || lastTimestamp == null){
//             mapStore.put(key, value);
//             timeStore.put(key, currentTimestamp);
//             return KeyValue.pair(key, value); 
//         }else if(currentTimestamp.minus(windowDuration).isAfter(lastTimestamp)){
//             mapStore.put(key, value);
//             timeStore.put(key, currentTimestamp);
//             return KeyValue.pair(key, value);
//         }else{
//             return null;
//         }
//     }

//     @Override
//     public void close() {
//         // No-op
//     }
// }