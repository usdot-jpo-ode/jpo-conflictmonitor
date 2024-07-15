package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.bsm.BsmTimestampDeltaAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithmFactory;

/**
 * Configuration defining {@link FactoryBean}s for locating Timestamp Delta algorithms.
 */
@Configuration
public class TimestampDeltaAlgorithms {

    @Bean
    public FactoryBean<?> bsmServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(BsmTimestampDeltaAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean FactoryBean<?> spatServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatTimestampDeltaAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean FactoryBean<?> mapServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapTimestampDeltaAlgorithmFactory.class);
        return factoryBean;
    }
}
