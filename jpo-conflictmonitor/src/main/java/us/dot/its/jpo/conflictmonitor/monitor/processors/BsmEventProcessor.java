package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;

public class BsmEventProcessor extends ContextualProcessor<String, OdeBsmData, String, BsmEvent> {


    private static final Logger logger = LoggerFactory.getLogger(BsmEventProcessor.class);
    
    private final String fStoreName = "bsm-event-state-store";
    private final Duration fPunctuationInterval = Duration.ofSeconds(10); // Check Every 10 Seconds
    private final long fSuppressTimeoutMillis = Duration.ofSeconds(10).toMillis(); // Emit event if no data for the last 10 seconds
    private TimestampedKeyValueStore<String, BsmEvent> stateStore;

    @Setter
    @Getter
    private PunctuationType punctuationType;



    @Override
    public void init(ProcessorContext<String, BsmEvent> context) {
        try {
            super.init(context);
            stateStore = (TimestampedKeyValueStore<String, BsmEvent>) context.getStateStore(fStoreName);
            context.schedule(fPunctuationInterval, punctuationType, this::punctuate);
        } catch (Exception e) {
            logger.error("Error initializing BsmEventProcessor", e);
        }
    }

    @Override
    public void process(Record<String, OdeBsmData> inputRecord) {
        String key = inputRecord.key();
        OdeBsmData value = inputRecord.value();
        long timestamp = inputRecord.timestamp();

        if(!validateBSM(value)){
            System.out.println("BSM Is not Valid");
            return;
        }

        try {
            // Key the BSM's based upon vehicle ID.
            key = key + "_" + ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
            
            ValueAndTimestamp<BsmEvent> record = stateStore.get(key);
            
            if (record != null) {
                BsmEvent event = record.value();
                long newRecTime = BsmTimestampExtractor.getBsmTimestamp(value);

                // If the new record is older than the last start bsm. Use the last start bsm instead.
                if(newRecTime < BsmTimestampExtractor.getBsmTimestamp(event.getStartingBsm())){
                    // If there is no ending BSM make the previous start bsm the end bsm 
                    if(event.getEndingBsm() == null){
                        event.setEndingBsm(event.getStartingBsm());
                    }
                    event.setStartingBsm(value);
                }else if(event.getEndingBsm() == null || newRecTime > BsmTimestampExtractor.getBsmTimestamp(event.getEndingBsm())){
                    // If the new record is more recent than the old record
                    event.setEndingBsm(value);
                }

                event.setEndingBsmTimestamp(timestamp);
                stateStore.put(key, ValueAndTimestamp.make(event, timestamp));
            } else {
                BsmEvent event = new BsmEvent(value);
                event.setStartingBsmTimestamp(timestamp);
                stateStore.put(key, ValueAndTimestamp.make(event, timestamp));
            }
        } catch (Exception e) {
            logger.error("Error in BsmEventProcessor.process", e);
        }
    }

    private void punctuate(long timestamp) {
        try (KeyValueIterator<String, ValueAndTimestamp<BsmEvent>> iterator = stateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, ValueAndTimestamp<BsmEvent>> item = iterator.next();
                var key = item.key;
                var value = item.value.value();
                var itemTimestamp = item.value.timestamp();
                if (timestamp - itemTimestamp > fSuppressTimeoutMillis) {
                    context().forward(new Record<>(key, value, timestamp));
                    stateStore.delete(key);
                }
            }
        } catch (Exception e) {
            logger.error("Error in BsmEventProcessor.punctuate", e);
        }
    }

    public boolean validateBSM(OdeBsmData bsm){
        if (bsm == null) return false;
        if (bsm.getPayload() == null) return false;
        if (!(bsm.getPayload() instanceof OdeBsmPayload)) return false;
        if (bsm.getMetadata() == null) return false;
        if (!(bsm.getMetadata() instanceof OdeBsmMetadata)) return false;
        if (bsm.getPayload().getData() == null) return false;
        if (!(bsm.getPayload().getData() instanceof J2735Bsm)) return false;


        J2735BsmCoreData core = ((J2735Bsm)bsm.getPayload().getData()).getCoreData();
        if (core == null) return false;

        OdeBsmMetadata metadata = (OdeBsmMetadata)bsm.getMetadata();

        if(core.getPosition().getLongitude() == null){
            return false;
        }

        if(core.getPosition().getLatitude() == null){
            return false;
        }

        if(core.getId() == null){
            return false;
        }

        if(core.getSecMark() == null){
            return false;
        }

        if(core.getSpeed() == null){
            return false;
        }

        if(core.getHeading() == null){
            return false;
        }

        if(metadata.getBsmSource() == null){
            return false;
        }

        if(metadata.getOriginIp() == null){
            return false;
        }

        if (metadata.getRecordGeneratedAt() == null){
            return false;
        }

        if (metadata.getOdeReceivedAt() == null) {
            return false;
        }

        return true;
    }



}