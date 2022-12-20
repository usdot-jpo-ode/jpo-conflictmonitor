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
    private KeyValueStore<String, SpatTimeChangeDetailAggregator> stateStore;
    private SpatTimeChangeDetailsParameters parameters;

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
        // Key the BSM's based upon vehicle ID.
        //key = key + "_" + ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
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
                            if(firstState.getSignalGroup() == secondState.getSignalGroup()){
                                if(firstState.getMinEndTime() > secondState.getMinEndTime() && firstState.getEventState() != secondState.getEventState()){
                                    //TimeChangeDetailsEvent event = buildEvent(first, second, firstState, secondState);
                                    TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                                    event.setRoadRegulatorID(first.getRegion());
                                    event.setIntersectionID(first.getIntersectionID());
                                    event.setSignalGroup(firstState.getSignalGroup());
                                    event.setFirstSpatTimestamp(first.getTimestamp());
                                    event.setSecondSpatTimestamp(second.getTimestamp());
                                    event.setFirstConflictingTimemark(firstState.getMinEndTime());
                                    event.setSecondConflictingTimemark(secondState.getMinEndTime());
                                    
                                    context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));
                                    
                                }
                                if(firstState.getMaxEndTime() < secondState.getMaxEndTime() && firstState.getEventState() != secondState.getEventState()){
                                    TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                                    event.setRoadRegulatorID(first.getRegion());
                                    event.setIntersectionID(first.getIntersectionID());
                                    event.setSignalGroup(firstState.getSignalGroup());
                                    event.setFirstSpatTimestamp(first.getTimestamp());
                                    event.setSecondSpatTimestamp(second.getTimestamp());
                                    event.setFirstConflictingTimemark(firstState.getMaxEndTime());
                                    event.setSecondConflictingTimemark(secondState.getMaxEndTime());
                                    
                                    context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));

                                }
                                if((firstState.getMinEndTime() != secondState.getMaxEndTime()) && isStateClearance(firstState.getEventState())){
                                    TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                                    event.setRoadRegulatorID(first.getRegion());
                                    event.setIntersectionID(first.getIntersectionID());
                                    event.setSignalGroup(firstState.getSignalGroup());
                                    event.setFirstSpatTimestamp(first.getTimestamp());
                                    event.setSecondSpatTimestamp(second.getTimestamp());
                                    event.setFirstConflictingTimemark(firstState.getMinEndTime());
                                    event.setSecondConflictingTimemark(secondState.getMaxEndTime());
                                    
                                    context().forward(new Record<>(key, event, event.getFirstSpatTimestamp()));
                                }

                                break;
                            }
                        }
                        if(firstState.getMaxEndTime() != firstState.getMinEndTime() && isStateClearance(firstState.getEventState())){
                            TimeChangeDetailsEvent event = new TimeChangeDetailsEvent();
                            event.setRoadRegulatorID(first.getRegion());
                            event.setIntersectionID(first.getIntersectionID());
                            event.setSignalGroup(firstState.getSignalGroup());
                            event.setFirstSpatTimestamp(first.getTimestamp());
                            event.setSecondSpatTimestamp(first.getTimestamp());
                            event.setFirstConflictingTimemark(firstState.getMaxEndTime());
                            event.setSecondConflictingTimemark(firstState.getMinEndTime());
                            
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

    
}