package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.time.Duration;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetail;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailPair;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;




// Pulls incoming spats and holds a buffer of messages.
// Acts as a Jitter Buffer to ensure messages are properly sequenced and ensure messages are processed in order. 
public class SpatSequenceProcessor extends ContextualProcessor<String, ProcessedSpat, String, TimeChangeDetailsEvent> {

    private final static Logger logger = LoggerFactory.getLogger(SpatSequenceProcessor.class);
    private KeyValueStore<String, SpatTimeChangeDetailAggregator> stateStore;
    private SpatTimeChangeDetailsParameters parameters;

    private final static String MIN_END_TIME_TIMEMARK = "minEndTime";
    private final static String MAX_END_TIME_TIMEMARK = "maxEndTime";

    public SpatSequenceProcessor(SpatTimeChangeDetailsParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        stateStore = (KeyValueStore<String, SpatTimeChangeDetailAggregator>) context.getStateStore(this.parameters.getSpatTimeChangeDetailsStateStoreName());
    }



    @Override
    public void process(Record<String, ProcessedSpat> record) {
        ProcessedSpat inputSpat = record.value();

        if (inputSpat == null) {
            logger.error("Null input spat");
            return;
        }
        String key = inputSpat.getOriginIp()+"_"+inputSpat.getRegion()+"_"+inputSpat.getIntersectionId();
        SpatTimeChangeDetailAggregator agg = stateStore.get(key);
        
        if (agg != null) {
            SpatTimeChangeDetailPair oldestPair = agg.add(SpatTimeChangeDetail.fromProcessedSpat(inputSpat));
            stateStore.put(key, agg);

            if(oldestPair != null){
                SpatTimeChangeDetail first = oldestPair.getFirst();
                SpatTimeChangeDetail second = oldestPair.getSecond();

                
                if(first.getStates() != null && second.getStates() != null){
                    for(SpatTimeChangeDetailState firstState: first.getStates()){
                        for(SpatTimeChangeDetailState secondState: second.getStates()){

                            // Check if its the same signal group
                            if(firstState.getSignalGroup() == secondState.getSignalGroup()){
                                System.out.println(firstState.getMaxEndTime() + " " + secondState.getMaxEndTime());
                                
                                // Check if its the same event state
                                if(firstState.getEventState() == secondState.getEventState()){

                                    // Check if both events have valid minEndTimes (unknown or a value)
                                    if(firstState.getMinEndTime() >=0 && secondState.getMinEndTime() >= 0){
                                        if(firstState.getMinEndTime() > secondState.getMinEndTime()){
                                            TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                                            event.setRoadRegulatorID(first.getRegion());
                                            event.setIntersectionID(first.getIntersectionID());
                                            event.setSignalGroup(firstState.getSignalGroup());
                                            event.setFirstSpatTimestamp(first.getTimestamp());
                                            event.setSecondSpatTimestamp(second.getTimestamp());
                                            event.setFirstConflictingTimemark(timeMarkFromUtc(firstState.getMinEndTime()));
                                            event.setSecondConflictingTimemark(timeMarkFromUtc(secondState.getMinEndTime()));
                                            event.setFirstState(firstState.getEventState());
                                            event.setSecondState(secondState.getEventState());
                                            event.setFirstTimeMarkType(MIN_END_TIME_TIMEMARK);
                                            event.setSecondTimeMarkType(MIN_END_TIME_TIMEMARK);
                                            
                                            context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));
                                            
                                        }

                                        // first state must have a valid time (not unknown) to generate an event.
                                        if(firstState.getMinEndTime() > 0 && firstState.getMinEndTime() != secondState.getMinEndTime() && isStateClearance(firstState.getEventState())){
                                            TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                                            event.setRoadRegulatorID(first.getRegion());
                                            event.setIntersectionID(first.getIntersectionID());
                                            event.setSignalGroup(firstState.getSignalGroup());
                                            event.setFirstSpatTimestamp(first.getTimestamp());
                                            event.setSecondSpatTimestamp(second.getTimestamp());
                                            event.setFirstConflictingTimemark(timeMarkFromUtc(firstState.getMinEndTime()));
                                            event.setSecondConflictingTimemark(timeMarkFromUtc(secondState.getMinEndTime()));
                                            event.setFirstState(firstState.getEventState());
                                            event.setSecondState(secondState.getEventState());
                                            event.setFirstTimeMarkType(MIN_END_TIME_TIMEMARK);
                                            event.setSecondTimeMarkType(MIN_END_TIME_TIMEMARK);
                                            
                                            context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));
                                        }
                                    }
                                    
                                    // check if both events have valid end times (unknown or a value)
                                    if(firstState.getMaxEndTime() >= 0 && secondState.getMaxEndTime() >= 0){
                                        if(firstState.getMaxEndTime() < secondState.getMaxEndTime()){
                                            TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                                            event.setRoadRegulatorID(first.getRegion());
                                            event.setIntersectionID(first.getIntersectionID());
                                            event.setSignalGroup(firstState.getSignalGroup());
                                            event.setFirstSpatTimestamp(first.getTimestamp());
                                            event.setSecondSpatTimestamp(second.getTimestamp());
                                            event.setFirstConflictingTimemark(timeMarkFromUtc(firstState.getMaxEndTime()));
                                            event.setSecondConflictingTimemark(timeMarkFromUtc(secondState.getMaxEndTime()));
                                            event.setFirstState(firstState.getEventState());
                                            event.setSecondState(secondState.getEventState());
                                            event.setFirstTimeMarkType(MAX_END_TIME_TIMEMARK);
                                            event.setSecondTimeMarkType(MAX_END_TIME_TIMEMARK);
                                            
                                            context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));
                                        }

                                        // First state must have a valid time (not unknown) to generate an event
                                        if(firstState.getMaxEndTime() >0 && firstState.getMaxEndTime() != secondState.getMaxEndTime() && isStateClearance(firstState.getEventState())){
                                            TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                                            event.setRoadRegulatorID(first.getRegion());
                                            event.setIntersectionID(first.getIntersectionID());
                                            event.setSignalGroup(firstState.getSignalGroup());
                                            event.setFirstSpatTimestamp(first.getTimestamp());
                                            event.setSecondSpatTimestamp(second.getTimestamp());
                                            event.setFirstConflictingTimemark(timeMarkFromUtc(firstState.getMaxEndTime()));
                                            event.setSecondConflictingTimemark(timeMarkFromUtc(secondState.getMaxEndTime()));
                                            event.setFirstState(firstState.getEventState());
                                            event.setSecondState(secondState.getEventState());
                                            event.setFirstTimeMarkType(MAX_END_TIME_TIMEMARK);
                                            event.setSecondTimeMarkType(MAX_END_TIME_TIMEMARK);
                                            
                                            context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        // States must have valid values in order for the event to be generated.
                        if(firstState.getMaxEndTime() > 0 && firstState.getMinEndTime() > 0 && firstState.getMaxEndTime() != firstState.getMinEndTime() && isStateClearance(firstState.getEventState())){
                            TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                            event.setRoadRegulatorID(first.getRegion());
                            event.setIntersectionID(first.getIntersectionID());
                            event.setSignalGroup(firstState.getSignalGroup());
                            event.setFirstSpatTimestamp(first.getTimestamp());
                            event.setSecondSpatTimestamp(first.getTimestamp());
                            event.setFirstConflictingTimemark(timeMarkFromUtc(firstState.getMaxEndTime()));
                            event.setSecondConflictingTimemark(timeMarkFromUtc(firstState.getMinEndTime()));
                            event.setFirstState(firstState.getEventState());
                            event.setFirstTimeMarkType(MAX_END_TIME_TIMEMARK);
                            event.setSecondTimeMarkType(MIN_END_TIME_TIMEMARK);
                            
                            context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));
                        }
                    }
                }
            }
            

        } else {
            agg = new SpatTimeChangeDetailAggregator(this.parameters.getJitterBufferSize());
            agg.add(SpatTimeChangeDetail.fromProcessedSpat(inputSpat));
            stateStore.put(key, agg);
        }
    }

    public boolean isStateClearance(J2735MovementPhaseState state){
        return state.equals(J2735MovementPhaseState.PERMISSIVE_CLEARANCE) || state.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE);
    }

    public long timeMarkFromUtc(long utcTimeMillis){
        return utcTimeMillis % (60 * 60 * 1000) / 100;
    }

    
}