package us.dot.its.jpo.conflictmonitor.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.mongo.ConfigInitializer;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.RepartitionTopology;
import us.dot.its.jpo.conflictmonitor.monitor.mongo.ConnectSourceCreator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit test for {@link MonitorServiceController}
 */
@RunWith(MockitoJUnitRunner.class)
public class MonitorServiceControllerTest {

    @Mock ConflictMonitorProperties conflictMonitorProperties;
    @Mock KafkaTemplate<String, String> kafkaTemplate;
    @Mock ConfigTopology configTopology;
    @Mock ConfigParameters configParameters;
    @Mock ConfigInitializer configInitializer;
    @Mock ConnectSourceCreator connectSourceCreator;

    @Mock RepartitionAlgorithmFactory repartitionAlgorithmFactory;
    @Mock RepartitionAlgorithm repartitionAlgorithm;
    RepartitionParameters repartitionParameters = new RepartitionParameters();

    @Mock MapValidationAlgorithmFactory mapValidationAlgorithmFactory;
    @Mock MapValidationAlgorithm mapValidationAlgorithm;
    MapValidationParameters mapValidationParameters = new MapValidationParameters();

    @Mock SpatValidationStreamsAlgorithmFactory spatValidationStreamsAlgorithmFactory;
    @Mock SpatValidationAlgorithm spatValidationAlgorithm;
    SpatValidationParameters spatValidationParameters = new SpatValidationParameters();

    @Mock SpatTimeChangeDetailsAlgorithmFactory spatTimeChangeDetailsAlgorithmFactory;
    @Mock SpatTimeChangeDetailsAlgorithm spatTimeChangeDetailsAlgorithm;
    SpatTimeChangeDetailsParameters spatTimeChangeDetailsParameters = new SpatTimeChangeDetailsParameters();

    @Mock MapSpatMessageAssessmentAlgorithmFactory mapSpatMessageAssessmentAlgorithmFactory;
    @Mock MapSpatMessageAssessmentAlgorithm mapSpatMessageAssessmentAlgorithm;
    MapSpatMessageAssessmentParameters mapSpatMessageAssessmentParameters = new MapSpatMessageAssessmentParameters();

    @Test
    public void testConstructor() {

        final String defaultAlgo = "default";
        
        when(conflictMonitorProperties.getRepartitionAlgorithmFactory()).thenReturn(repartitionAlgorithmFactory);
        when(conflictMonitorProperties.getRepartitionAlgorithm()).thenReturn(defaultAlgo);
        when(repartitionAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(repartitionAlgorithm);
        when(conflictMonitorProperties.getRepartitionAlgorithmParameters()).thenReturn(repartitionParameters);
        
        when(conflictMonitorProperties.getMapValidationAlgorithmFactory()).thenReturn(mapValidationAlgorithmFactory);
        when(conflictMonitorProperties.getMapValidationAlgorithm()).thenReturn(defaultAlgo);
        when(mapValidationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapValidationAlgorithm);
        when(conflictMonitorProperties.getMapValidationParameters()).thenReturn(mapValidationParameters);

        when(conflictMonitorProperties.getSpatValidationAlgorithmFactory()).thenReturn(spatValidationStreamsAlgorithmFactory);
        when(conflictMonitorProperties.getSpatValidationAlgorithm()).thenReturn(defaultAlgo);
        when(spatValidationStreamsAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatValidationAlgorithm);
        when(conflictMonitorProperties.getSpatValidationParameters()).thenReturn(spatValidationParameters);

        when(conflictMonitorProperties.getSpatTimeChangeDetailsAlgorithmFactory()).thenReturn(spatTimeChangeDetailsAlgorithmFactory);
        when(conflictMonitorProperties.getSpatTimeChangeDetailsAlgorithm()).thenReturn(defaultAlgo);
        when(spatTimeChangeDetailsAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatTimeChangeDetailsAlgorithm);
        when(conflictMonitorProperties.getSpatTimeChangeDetailsParameters()).thenReturn(spatTimeChangeDetailsParameters);

        when(conflictMonitorProperties.getMapSpatMessageAssessmentAlgorithmFactory()).thenReturn(mapSpatMessageAssessmentAlgorithmFactory);
        when(conflictMonitorProperties.getMapSpatMessageAssessmentAlgorithm()).thenReturn(defaultAlgo);
        when(mapSpatMessageAssessmentAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapSpatMessageAssessmentAlgorithm);
        when(conflictMonitorProperties.getMapSpatMessageAssessmentParameters()).thenReturn(mapSpatMessageAssessmentParameters);

        var monitorServiceController = new MonitorServiceController(
                conflictMonitorProperties,
                kafkaTemplate,
                configTopology,
                configParameters,
                configInitializer,
                connectSourceCreator
        );
        assertThat(monitorServiceController, notNullValue());

        // Check all algorithms were started
        verify(repartitionAlgorithm, times(1)).start();
        verify(mapValidationAlgorithm, times(1)).start();
        verify(spatValidationAlgorithm, times(1)).start();
        verify(spatTimeChangeDetailsAlgorithm, times(1)).start();
        verify(mapSpatMessageAssessmentAlgorithm, times(1)).start();

    }
    
}
