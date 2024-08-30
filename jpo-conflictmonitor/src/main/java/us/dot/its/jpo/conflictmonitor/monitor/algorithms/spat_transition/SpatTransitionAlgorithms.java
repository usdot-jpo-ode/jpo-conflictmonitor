package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithmFactory;

/**
 * Configuration defining {@link FactoryBean}s for locating Spat Transition algorithms.
 */
@Configuration
public class SpatTransitionAlgorithms {

    @Bean
    FactoryBean<?> spatTransitionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatTransitionAlgorithmFactory.class);
        return factoryBean;
    }

}
