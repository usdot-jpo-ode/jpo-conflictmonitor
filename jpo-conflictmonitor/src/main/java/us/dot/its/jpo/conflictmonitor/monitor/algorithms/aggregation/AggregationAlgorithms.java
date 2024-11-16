package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.IntersectionReferenceAlignmentAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalGroupAlignmentAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.map.MapMinimumDataAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationAlgorithmFactory;


@Configuration
public class AggregationAlgorithms {

    @Bean
    FactoryBean<?> spatMinimumDataServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatMinimumDataAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> mapMinimumDataServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapMinimumDataAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> eventStateProgressionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(EventStateProgressionAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> intersectionReferenceAlignmentServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(IntersectionReferenceAlignmentAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> signalGroupAlignmentServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SignalGroupAlignmentAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> signalStateConflictServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SignalStateConflictAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> timeChangeDetailsServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(TimeChangeDetailsAggregationAlgorithmFactory.class);
        return factoryBean;
    }

}
