package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParametersFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParametersFactory;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;

/**
 * Beans related to broadcast rate configuration parameters.
 */
@Configuration
public class BroadcastRateConfigurations {

    @Bean(MAP_BROADCAST_RATE_PARAMETERS_FACTORY) 
    public FactoryBean<?> mapAlgorithmFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapBroadcastRateParametersFactory.class);
        return factoryBean;
    }

    @Bean(SPAT_BROADCAST_RATE_PARAMETERS_FACTORY)
    public FactoryBean<?> spatAlgorithmFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatBroadcastRateParametersFactory.class);
        return factoryBean;
    }

    /**
     * @return Default MAP broadcast rate parameters per the requirements with 10 second wide, 5 second hopping window
     */
    @Bean(DEFAULT_MAP_BROADCAST_RATE_PARAMETERS)
    public MapBroadcastRateParameters defaultMapBroadcastRateParamaters() {
        var params = new MapBroadcastRateParameters();
        params.setInputTopicName("topic.OdeMapJson");
        params.setOutputCountTopicName("topic.CmMapCounts");
        params.setOutputEventTopicName("topic.CmMapBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setLowerBound(9);
        params.setUpperBound(11);
        return params;
    }

    /**
     * @return Alernate parameters with a tumbling window (output interval same as size of window)
     */
    @Bean(ALTERNATE_MAP_BROADCAST_RATE_PARAMETERS)
    public MapBroadcastRateParameters alternateMapBroadcastRateParamaters() {
        var params = new MapBroadcastRateParameters();
        params.setInputTopicName("topic.OdeMapJson");
        params.setOutputCountTopicName("topic.CmMapCounts");
        params.setOutputEventTopicName("topic.CmMapBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(10);
        params.setLowerBound(9);
        params.setUpperBound(11);
        return params;
    }

    @Bean(DEFAULT_SPAT_BROADCAST_RATE_PARAMETERS)
    public SpatBroadcastRateParameters defaultSpatBroadcastRateParamaters() {
        var params = new SpatBroadcastRateParameters();
        params.setInputTopicName("topic.OdeSpatJson");
        params.setOutputCountTopicName("topic.CmSpatCounts");
        params.setOutputEventTopicName("topic.CmSpatBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setLowerBound(90);
        params.setUpperBound(110);
        return params;
    }
    
}
