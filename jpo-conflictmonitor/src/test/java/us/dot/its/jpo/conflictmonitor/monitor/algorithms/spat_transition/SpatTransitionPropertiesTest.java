package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.PhaseStateTransition;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.PhaseStateTransitionList;

import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
@Slf4j
public class SpatTransitionPropertiesTest {

    @Autowired
    private SpatTransitionParameters params;

    private final int expectedNumberOfEntries = 28;

    // Test that the 'illegalSpatTransitionList' data structure can be loaded
    // from 'application.yaml'
    @Test
    public void testIllegalSpatTransitionList() {
        assertThat(params, notNullValue());
        PhaseStateTransitionList theList = params.getIllegalSpatTransitionList();
        log.info("PhaseStateTransitionList: {}", theList);
        assertThat(theList, allOf(notNullValue(), hasSize(equalTo(expectedNumberOfEntries))));

        var set = new HashSet<PhaseStateTransition>();
        for (var state : theList) {
            assertThat(state, hasProperty("stateA", notNullValue()));
            assertThat(state, hasProperty("stateB", notNullValue()));
            set.add(state);
        }

        // Check for duplicate entries: the number in the deduplicated set should be same as the list
        assertThat("Duplicate entries", set, hasSize(equalTo(expectedNumberOfEntries)));



    }

}
