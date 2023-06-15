package us.dot.its.jpo.conflictmonitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.apache.kafka.streams.KafkaStreams.State;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health.KafkaStreamsStateChangeEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.app_health.KafkaStreamsAnomalyNotification;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/**
 * Unit tests for {@link StateChangeHandler}.
 */
@RunWith(Parameterized.class)
public class StateChangeHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(StateChangeHandlerTest.class);

    // Use MockitoRule to initialize mocks to allow using parameterized JUnit runner
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    ListenableFuture<SendResult<String, String>> mockSendResult;

    final String topic = "testTopic";
    final String notificationTopic = "testNotificationTopic";
    final String topology = "testTopology";
    
    @Captor
    ArgumentCaptor<String> eventCaptor;

    @Captor
    ArgumentCaptor<String> notificationKeyCaptor;

    @Captor
    ArgumentCaptor<String> notificationCaptor;

    final ObjectMapper mapper = DateJsonMapper.getInstance();

    State oldState;
    State newState;
    boolean expectNotification;

    public StateChangeHandlerTest(State oldState, State newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    @Parameters
    public static Collection<Object[]> getParams() {
        return Arrays.asList(new Object[][] {
            { State.NOT_RUNNING, State.RUNNING},
            { State.RUNNING, State.ERROR},
            { State.RUNNING, State.REBALANCING}
        });
    }
    
    @Test
    public void testOnChange() throws JsonMappingException, JsonProcessingException {
        when(kafkaTemplate.send(eq(topic), eq(topology), anyString())).thenReturn(mockSendResult);
        when(kafkaTemplate.send(eq(notificationTopic), anyString(), anyString())).thenReturn(mockSendResult);
       
        
        StateChangeHandler handler = new StateChangeHandler(kafkaTemplate, topology, topic, notificationTopic);
        assertThat(handler, notNullValue());
        
        handler.onChange(newState, oldState);
        
        // Verify that both an event and notification were sent
        verify(kafkaTemplate, times(1)).send(eq(topic), eq(topology), 
            eventCaptor.capture());
        
        
        String event = eventCaptor.getValue();
        assertThat(event, notNullValue());
        logger.info("Event: {}", event);
        
        // Deserialize and validate the event
        var eventObj = mapper.readValue(event, KafkaStreamsStateChangeEvent.class);
        assertThat(eventObj, notNullValue());
        assertThat(eventObj.getOldState(), equalTo(oldState.toString()));
        assertThat(eventObj.getNewState(), equalTo(newState.toString()));
        assertThat(eventObj.getTopology(), equalTo(topology));

        // Notification expected if new state is ERROR
        if (State.ERROR.equals(newState)) {
            verify(kafkaTemplate, times(1)).send(eq(notificationTopic), 
                                notificationKeyCaptor.capture(), notificationCaptor.capture());
            // Deserialize and validate the notification
            String notificationKey = notificationKeyCaptor.getValue();
            String notification = notificationCaptor.getValue();
            assertThat(notificationKey, notNullValue());
            assertThat(notification, notNullValue());
            logger.info("Notification Key: {}", notificationKey);
            logger.info("Notification: {}" , notification);
            var notificationObj = mapper.readValue(notification, KafkaStreamsAnomalyNotification.class);
            assertThat(notificationObj, notNullValue());
            assertThat(notificationObj.getUniqueId(), equalTo(notificationKey));
        } else {
            // Not an error, notification should not have been sent
            verify(kafkaTemplate, times(0)).send(eq(notificationTopic),
                                anyString(), anyString());
        }
    }

    
    
    
}
