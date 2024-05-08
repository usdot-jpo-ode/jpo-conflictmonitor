package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpatRevisionCounterAlgorithms {
    @Bean
    public FactoryBean<?> spatRevisionCounterServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatRevisionCounterAlgorithmFactory.class);
        return factoryBean;
    }
}