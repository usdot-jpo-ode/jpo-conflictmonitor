package us.dot.its.jpo.conflictmonitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.kafka.streams.KafkaStreams.State;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
@RunWith(MockitoJUnitRunner.class)
public class StateChangeHandlerTest {

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
    
    @Test
    public void testOnChange() throws JsonMappingException, JsonProcessingException {
        when(kafkaTemplate.send(eq(topic), eq(topology), anyString())).thenReturn(mockSendResult);
        when(kafkaTemplate.send(eq(notificationTopic), anyString(), anyString())).thenReturn(mockSendResult);
       
        final State oldState = State.RUNNING;
        final State newState = State.ERROR;
        
        StateChangeHandler handler = new StateChangeHandler(kafkaTemplate, topology, topic, notificationTopic);
        assertThat(handler, notNullValue());
        
        handler.onChange(newState, oldState);
        
        verify(kafkaTemplate, times(1)).send(eq(topic), eq(topology), 
            eventCaptor.capture());
        String event = eventCaptor.getValue();
        assertThat(event, notNullValue());
        System.out.println(event);
        
        // Deserialize and verify the event
        var eventObj = mapper.readValue(event, KafkaStreamsStateChangeEvent.class);
        assertThat(eventObj, notNullValue());
        assertThat(eventObj.getOldState(), equalTo(oldState.toString()));
        assertThat(eventObj.getNewState(), equalTo(newState.toString()));
    

        verify(kafkaTemplate, times(1)).send(eq(notificationTopic), 
            notificationKeyCaptor.capture(), notificationCaptor.capture());

        // Deserialize and verify the notification
        String notificationKey = notificationKeyCaptor.getValue();
        String notification = notificationCaptor.getValue();
        assertThat(notificationKey, notNullValue());
        assertThat(notification, notNullValue());
        System.out.println(notificationKey);
        System.out.println(notification);
        var notificationObj = mapper.readValue(notification, KafkaStreamsAnomalyNotification.class);
        assertThat(notificationObj, notNullValue());
        assertThat(notificationObj.getUniqueId(), equalTo(notificationKey));
    }

    
    
}
