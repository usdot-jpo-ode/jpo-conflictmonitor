package us.dot.its.jpo.conflictmonitor.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ConflictMonitorPropertiesTest {
    
    @Autowired
    private ConflictMonitorProperties properties;

    @Test
    public void testPropertiesInjected() {
        assertThat(properties, notNullValue());
    }

    @Test
    public void testMapBroadcastRateAlgorithm() {
        assertThat(properties.getMapBroadcastRateAlgorithm(), anyOf(equalTo(DEFAULT_MAP_BROADCAST_RATE_ALGORITHM), equalTo(ALTERNATE_MAP_BROADCAST_RATE_ALGORITHM)));
    }

    @Test
    public void testMapBroadcastRateParameters() {
        assertThat(properties.getMapBroadcastRateParameters(), anyOf(equalTo(DEFAULT_MAP_BROADCAST_RATE_PARAMETERS), equalTo(DEBUG_MAP_BROADCAST_RATE_PARAMETERS)));
    }

    @Test
    public void testSpatBroadcastRateAlgorithm() {
        assertThat(properties.getSpatBroadcastRateAlgorithm(), anyOf(equalTo(DEFAULT_SPAT_BROADCAST_RATE_ALGORITHM), equalTo(ALTERNATE_SPAT_BROADCAST_RATE_ALGORITHM)));
    }

    @Test
    public void testSpatBroadcastRateParameters() {
        assertThat(properties.getSpatBroadcastRateParameters(), anyOf(equalTo(DEFAULT_SPAT_BROADCAST_RATE_PARAMETERS), equalTo(DEBUG_SPAT_BROADCAST_RATE_PARAMETERS)));
    }
    
}
