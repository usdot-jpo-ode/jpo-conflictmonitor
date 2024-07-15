package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithmFactory;

/**
 * Configuration defining {@link FactoryBean}s for locating Broadcast Rate algorithms.
 */
@Configuration
public class ValidationAlgorithms {
    
    @Bean
    public FactoryBean<?> mapServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapValidationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    public FactoryBean<?> spatServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatValidationStreamsAlgorithmFactory.class);
        return factoryBean;
    }

   

}
