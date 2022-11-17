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

    @Bean 
    public FactoryBean<?> mapAlgorithmFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapBroadcastRateParametersFactory.class);
        return factoryBean;
    }

    @Bean
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
        params.setOutputEventTopicName("topic.CmMapBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setGracePeriodMilliseconds(100);
        params.setLowerBound(9);
        params.setUpperBound(11);
        params.setDebug(false);
        return params;
    }

 

    @Bean(DEBUG_MAP_BROADCAST_RATE_PARAMETERS)
    public MapBroadcastRateParameters debugMapBroadcastRateParameters() {
        var params = defaultMapBroadcastRateParamaters();
        params.setDebug(true);
        return params;
    }



    @Bean(DEFAULT_SPAT_BROADCAST_RATE_PARAMETERS)
    public SpatBroadcastRateParameters defaultSpatBroadcastRateParamaters() {
        var params = new SpatBroadcastRateParameters();
        params.setInputTopicName("topic.OdeSpatJson");
        params.setOutputEventTopicName("topic.CmSpatBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setGracePeriodMilliseconds(100);
        params.setLowerBound(90);
        params.setUpperBound(110);
        params.setDebug(false);
        return params;
    }

    @Bean(DEBUG_SPAT_BROADCAST_RATE_PARAMETERS)
    public SpatBroadcastRateParameters debugSpatBroadcastRateParameters() {
        var params = defaultSpatBroadcastRateParamaters();
        params.setDebug(true);
        return params;
    }
    
}
