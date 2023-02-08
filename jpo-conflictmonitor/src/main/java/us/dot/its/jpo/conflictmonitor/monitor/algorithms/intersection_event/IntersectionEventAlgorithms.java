package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration defining {@link FactoryBean}s for locating {@link IntersectionEventAlgorithm}s
 */
@Configuration
public class IntersectionEventAlgorithms {
    
    @Bean
    public FactoryBean<?> intersectionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(IntersectionEventAlgorithmFactory.class);
        return factoryBean;
    }
}
