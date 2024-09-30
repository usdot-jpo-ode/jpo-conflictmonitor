package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BsmRevisionCounterAlgorithms {
    @Bean
    public FactoryBean<?> bsmRevisionCounterServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(BsmRevisionCounterAlgorithmFactory.class);
        return factoryBean;
    }
}