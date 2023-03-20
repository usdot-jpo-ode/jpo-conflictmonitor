package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.time.Duration;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;

public class BsmEventProcessor extends AbstractProcessor<String, OdeBsmData> {


    private static final Logger logger = LoggerFactory.getLogger(BsmEventProcessor.class);
    
    private final String fStoreName = "bsm-event-state-store";
    private final Duration fPunctuationInterval = Duration.ofSeconds(10); // Check Every 10 Seconds
    private final long fSuppressTimeoutMillis = Duration.ofSeconds(10).toMillis(); // Emit event if no data for the last 10 seconds
    private TimestampedKeyValueStore<String, BsmEvent> stateStore;

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        stateStore = (TimestampedKeyValueStore<String, BsmEvent>) context.getStateStore(fStoreName);
        context.schedule(fPunctuationInterval, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
    }

    @Override
    public void process(String key, OdeBsmData value) {
        if(!validateBSM(value)){
            System.out.println("BSM Is not Valid");
            return;
        }


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
            event.setEndingBsmTimestamp(context().timestamp());
            stateStore.put(key, ValueAndTimestamp.make(event, context().timestamp()));
        } else {
            BsmEvent event = new BsmEvent(value);
            event.setStartingBsmTimestamp(context.timestamp());
            stateStore.put(key, ValueAndTimestamp.make(event, context().timestamp()));
        }
    }

    private void punctuate(long timestamp) {
        try (KeyValueIterator<String, ValueAndTimestamp<BsmEvent>> iterator = stateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, ValueAndTimestamp<BsmEvent>> record = iterator.next();
                if (context().timestamp() - record.value.timestamp() > fSuppressTimeoutMillis) {
                    context().forward(record.key, record.value.value());
                    stateStore.delete(record.key);
                }
            }
        }
    }

    private boolean validateBSM(OdeBsmData bsm){
        J2735BsmCoreData core = ((J2735Bsm)bsm.getPayload().getData()).getCoreData();
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

        if(metadata.getBsmSource().name() == null){
            return false;
        }

        if(metadata.getOriginIp() == null){
            return false;
        }

        if(metadata.getRecordGeneratedAt() == null){
            return false;
        }

        return true;
    }
}