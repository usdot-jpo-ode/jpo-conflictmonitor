package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParametersFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParameters;

@Configuration
public class BroadcastRateConfigurations {

    @Bean("mapBroadcastRateParametersFactory") 
    public FactoryBean<?> serviceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapBroadcastRateParametersFactory.class);
        return factoryBean;
    }

    /**
     * @return Default MAP broadcast rate parameters per the requirements with 10 second wide, 5 second hopping window
     */
    @Bean("defaultMapBroadcastRateParameters")
    public MapBroadcastRateParameters defaultMapBroadcastRateParamaters() {
        var params = new MapBroadcastRateParameters();
        params.setInputTopicName("topic.OdeMapJson");
        params.setOutputTopicName("topic.ConflictMonitor.MapBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setLowerBound(9);
        params.setUpperBound(11);
        return params;
    }

    /**
     * @return Alernate parameters with a tumbling window (output interval same as size of window)
     */
    @Bean("alternateMapBroadcastRateParameters")
    public MapBroadcastRateParameters alternateMapBroadcastRateParamaters() {
        var params = new MapBroadcastRateParameters();
        params.setInputTopicName("topic.OdeMapJson");
        params.setOutputTopicName("topic.ConflictMonitor.MapBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(10);
        params.setLowerBound(9);
        params.setUpperBound(11);
        return params;
    }

    @Bean("defaultSpatBroadcastRateParameters")
    public SpatBroadcastRateParameters defaultSpatBroadcastRateParamaters() {
        var params = new SpatBroadcastRateParameters();
        params.setInputTopicName("topic.OdeSpatJson");
        params.setOutputTopicName("topic.ConflictMonitor.SpatBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setLowerBound(90);
        params.setUpperBound(110);
        return params;
    }
    
}
