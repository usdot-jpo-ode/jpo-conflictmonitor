package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration defining {@link FactoryBean}s for locating Event State Progression algorithms.
 */
@Configuration
public class EventStateProgressionAlgorithms {

    @Bean
    FactoryBean<?> eventStateProgressionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(EventStateProgressionAlgorithmFactory.class);
        return factoryBean;
    }

}
