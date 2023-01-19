package us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepartitionAlgorithms {
    @Bean
    public FactoryBean<?> repartitionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(RepartitionAlgorithmFactory.class);
        return factoryBean;
    }
}