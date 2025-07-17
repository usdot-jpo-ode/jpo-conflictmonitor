package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.map.MapTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithmFactory;



/**
 * Configuration defining {@link org.springframework.beans.factory.FactoryBean}s for locating Broadcast Rate algorithms.
 */
@Configuration
public class TimeChangeDetailsAlgorithms {
    
    @Bean
    public FactoryBean<?> mapTCDServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapTimeChangeDetailsAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    public FactoryBean<?> spatTCDServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatTimeChangeDetailsAlgorithmFactory.class);
        return factoryBean;
    }

   

}
