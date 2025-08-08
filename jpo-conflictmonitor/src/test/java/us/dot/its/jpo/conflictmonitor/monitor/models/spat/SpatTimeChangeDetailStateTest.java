package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementPhaseState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.TimingChangeDetails;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static us.dot.its.jpo.conflictmonitor.monitor.utils.SpatUtils.phaseStateEnum;

/**
 * Unit tests for {@link SpatTimeChangeDetailState}
 */
@RunWith(Parameterized.class)
public class SpatTimeChangeDetailStateTest {

    ProcessedMovementState inputState;
    SpatTimeChangeDetailState expectedResult;
    final static ZonedDateTime MAX_TIME = ZonedDateTime.parse("2023-03-13T00:00:30.999Z");
    final static ZonedDateTime MIN_TIME = ZonedDateTime.parse("2023-03-13T00:00:15.555Z");
    final static MovementPhaseState EVENT_STATE = MovementPhaseState.PERMISSIVE_MOVEMENT_ALLOWED;
    final static Integer SIGNAL_GROUP = 10;

    public SpatTimeChangeDetailStateTest(ProcessedMovementState inputState, SpatTimeChangeDetailState expectedResult) {
        this.inputState = inputState;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> getParams() {
        var params = new ArrayList<Object[]>();
        params.add(state(null, null, null, null));
        params.add(state(MAX_TIME, null, null, SIGNAL_GROUP));
        params.add(state(null, MIN_TIME, null, SIGNAL_GROUP));
        params.add(state(MAX_TIME, MIN_TIME, null, SIGNAL_GROUP));
        params.add(state(null, null, EVENT_STATE, SIGNAL_GROUP));
        params.add(state(MAX_TIME, MIN_TIME, EVENT_STATE, SIGNAL_GROUP));
        return params;
    }

    public static Object[] state(ZonedDateTime maxTime, ZonedDateTime minTime,
                                 MovementPhaseState eventState, Integer signalGroup) {

        // Construct MovementState
        ProcessedMovementState state = new ProcessedMovementState();
        state.setSignalGroup(signalGroup);
        ProcessedMovementEvent event = new ProcessedMovementEvent();
        
        TimingChangeDetails timing = new TimingChangeDetails();
        timing.setMaxEndTime(maxTime);
        timing.setMinEndTime(minTime);
        event.setTiming(timing);
        
        event.setEventState(eventState);
        state.setStateTimeSpeed(Collections.singletonList(event));

        // Construct expected SpatTimeChangeDetailState
        SpatTimeChangeDetailState expectedResult = new SpatTimeChangeDetailState();
        if (signalGroup != null) {
            expectedResult.setSignalGroup(signalGroup);
        }
        if (maxTime != null) {
            long millis = maxTime.toInstant().toEpochMilli();
            if(millis > 0){
                expectedResult.setMaxEndTime(millis);
            }else{
                expectedResult.setMaxEndTime(0);
            }
            
        }else{
            expectedResult.setMaxEndTime(-1);
        }

        if (minTime != null) {
            long millis = minTime.toInstant().toEpochMilli();
            if(millis > 0){
                expectedResult.setMinEndTime(millis);
            }else{
                expectedResult.setMinEndTime(0);
            }
        }else{
            expectedResult.setMinEndTime(-1);
        }

        expectedResult.setEventState(phaseStateEnum(eventState));
        System.out.println(expectedResult);
        return new Object[] { state, expectedResult};
    }

   
    
    /**
     * Test that {@link SpatTimeChangeDetailState#fromMovementState(ProcessedMovementState)} can deal with nulls
     */
    @Test
    public void testFromMovementState() {
        SpatTimeChangeDetailState result = SpatTimeChangeDetailState.fromMovementState(inputState);
        assertThat(result, equalTo(expectedResult));
    }
}
