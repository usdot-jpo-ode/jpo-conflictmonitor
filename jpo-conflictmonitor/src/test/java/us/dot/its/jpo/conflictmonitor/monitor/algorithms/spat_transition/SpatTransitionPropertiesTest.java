package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatTransition;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatTransitionList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
public class SpatTransitionPropertiesTest {

    @Autowired
    private SpatTransitionParameters params;


    // Test that the 'illegalSpatTransitionList' data structure can be loaded
    // from 'application.yaml'
    @Test
    public void testIllegalSpatTransitionList() {
        assertThat(params, notNullValue());
        SpatTransitionList theList = params.getIllegalSpatTransitionList();
        assertThat(theList, allOf(notNullValue(), hasSize(greaterThan(1))));

        SpatTransition st1 = theList.getFirst();
        assertThat(st1, hasProperty("firstState", equalTo(PERMISSIVE_MOVEMENT_ALLOWED)));
        assertThat(st1, hasProperty("secondState", equalTo(STOP_AND_REMAIN)));

        SpatTransition st2 = theList.get(1);
        assertThat(st2, hasProperty("firstState", equalTo(PROTECTED_MOVEMENT_ALLOWED)));
        assertThat(st2, hasProperty("secondState", equalTo(STOP_AND_REMAIN)));


    }

}
