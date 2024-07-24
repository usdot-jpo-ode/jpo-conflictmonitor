package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanes;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPair;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPairList;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

public abstract class BaseGenericJsonDeserializer_ConcurrentPermissiveTest {

    public abstract void testDeserialize_ConcurrentPermissive();

    protected void testConfigResult(IntersectionConfig<?> config) {
        assertThat(config, notNullValue());
        assertThat(config, hasProperty("type", equalTo("us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPairList")));
        assertThat(config, hasProperty("key", equalTo("map.spat.message.assessment.concurrentPermissiveList")));
        assertThat(config, hasProperty("roadRegulatorID", equalTo(10)));
        assertThat(config, hasProperty("intersectionID", equalTo(101010)));
        assertThat(config, hasProperty("units", equalTo(UnitsEnum.NONE)));
        assertThat(config, hasProperty("description", equalTo("List of pairs of lane connections that are allowed concurrently permissive for an intersection.")));
        assertThat(config, hasProperty("updateType", equalTo(UpdateType.INTERSECTION)));
        assertThat(config, hasProperty("value", instanceOf(ConnectedLanesPairList.class)));

        ConnectedLanesPairList value = (ConnectedLanesPairList)config.getValue();
        assertThat(value, hasSize(2));
        for (ConnectedLanesPair item : value) {
            assertThat(item, hasProperty("first", notNullValue()));
            ConnectedLanes first = item.getFirst();
            assertThat(first, hasProperty("ingressLaneID", greaterThan(0)));
            assertThat(first, hasProperty("egressLaneID", greaterThan(0)));
            assertThat(item, hasProperty("second", notNullValue()));
            ConnectedLanes second = item.getSecond();
            assertThat(second, hasProperty("ingressLaneID", greaterThan(0)));
            assertThat(second, hasProperty("egressLaneID", greaterThan(0)));

        }
    }

    final static String configString = """
            {
              "key": "map.spat.message.assessment.concurrentPermissiveList",
              "category": "category",
              "roadRegulatorID": 10,
              "intersectionID": 101010,
              "value": [
                {
                  "first": {
                    "ingressLaneID": 1,
                    "egressLaneID": 2
                  },
                  "second": {
                    "ingressLaneID": 3,
                    "egressLaneID": 4
                  }
                },
                {
                  "first": {
                    "ingressLaneID": 9,
                    "egressLaneID": 10
                  },
                  "second": {
                    "ingressLaneID": 11,
                    "egressLaneID": 12
                  }
                }
              ],
              "type": "us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPairList",
              "units": "NONE",
              "description": "List of pairs of lane connections that are allowed concurrently permissive for an intersection.",
              "updateType": "INTERSECTION"
            }
            """;

}
