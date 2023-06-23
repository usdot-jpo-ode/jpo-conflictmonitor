package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BsmEventAlgorithms {
    @Bean
    public FactoryBean<?> bsmEventServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(BsmEventAlgorithmFactory.class);
        return factoryBean;
    }
}
