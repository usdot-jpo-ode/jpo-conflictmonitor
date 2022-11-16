package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithmFactory;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;

/**
 * Configuration defining {@link FactoryBeans}s for locating Broadcast Rate algorithms.
 */
@Configuration
public class BroadcastRateAlgorithms {
    
    @Bean
    public FactoryBean<?> mapServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapBroadcastRateAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    public FactoryBean<?> spatServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatBroadcastRateAlgorithmFactory.class);
        return factoryBean;
    }

   

}
