package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.TimingChangeDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementEvent;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link SpatTimeChangeDetailState}
 */
@RunWith(Parameterized.class)
public class SpatTimeChangeDetailStateTest {

    MovementState inputState;
    SpatTimeChangeDetailState expectedResult;
    final static ZonedDateTime MAX_TIME = ZonedDateTime.parse("2023-03-13T00:00:30.999Z");
    final static ZonedDateTime MIN_TIME = ZonedDateTime.parse("2023-03-13T00:00:15.555Z");
    final static J2735MovementPhaseState EVENT_STATE = J2735MovementPhaseState.PERMISSIVE_MOVEMENT_ALLOWED;
    final static Integer SIGNAL_GROUP = 10;

    public SpatTimeChangeDetailStateTest(MovementState inputState, SpatTimeChangeDetailState expectedResult) {
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
            J2735MovementPhaseState eventState, Integer signalGroup) {

        // Construct MovementState
        MovementState state = new MovementState();
        state.setSignalGroup(signalGroup);
        MovementEvent event = new MovementEvent();
        if (maxTime != null || minTime != null) {
            TimingChangeDetails timing = new TimingChangeDetails();
            timing.setMaxEndTime(maxTime);
            timing.setMinEndTime(minTime);
            event.setTiming(timing);
        }
        event.setEventState(eventState);
        state.setStateTimeSpeed(Collections.singletonList(event));

        // Consturct expected SpatTimeChangeDetailState
        SpatTimeChangeDetailState expectedResult = new SpatTimeChangeDetailState();
        if (signalGroup != null) {
            expectedResult.setSignalGroup(signalGroup.intValue());
        }
        if (maxTime != null) {
            expectedResult.setMaxEndTime(maxTime.toInstant().toEpochMilli());
        }
        if (minTime != null) {
            expectedResult.setMinEndTime(minTime.toInstant().toEpochMilli());
        }
        expectedResult.setEventState(eventState);
        
        return new Object[] { state, expectedResult};
    }

   
    
    /**
     * Test that {@link SpatTimeChangeDetailState#fromMovementState(MovementState)} can deal with nulls
     */
    @Test
    public void testFromMovementState() {
        SpatTimeChangeDetailState result = SpatTimeChangeDetailState.fromMovementState(inputState);
        assertThat(result, equalTo(expectedResult));
    }
}
