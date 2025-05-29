package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.IntersectionReferenceAlignmentAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalGroupAlignmentAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.map.MapMinimumDataAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationAlgorithmFactory;


@Configuration
public class AggregationAlgorithms {

    @Bean
    FactoryBean<?> spatMinimumDataAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatMinimumDataAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> mapMinimumDataAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapMinimumDataAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> eventStateProgressionAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(EventStateProgressionAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> intersectionReferenceAlignmentAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(IntersectionReferenceAlignmentAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> signalGroupAlignmentAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SignalGroupAlignmentAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> signalStateConflictAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SignalStateConflictAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> timeChangeDetailsAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(TimeChangeDetailsAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> bsmMessageCountProgressionAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(BsmMessageCountProgressionAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> spatMessageCountProgressionAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatMessageCountProgressionAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> mapMessageCountProgressionAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapMessageCountProgressionAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> revocableEnabledLaneAlignmentAggregationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(RevocableEnabledLaneAlignmentAggregationAlgorithmFactory.class);
        return factoryBean;
    }
}
