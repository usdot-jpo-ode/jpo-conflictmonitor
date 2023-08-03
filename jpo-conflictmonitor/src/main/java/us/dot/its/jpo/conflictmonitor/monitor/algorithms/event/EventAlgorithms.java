package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventAlgorithms {
    @Bean
    public FactoryBean<?> eventServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(EventAlgorithmFactory.class);
        return factoryBean;
    }
}