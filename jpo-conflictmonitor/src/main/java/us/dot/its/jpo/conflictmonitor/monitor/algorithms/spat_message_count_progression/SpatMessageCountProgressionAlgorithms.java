package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpatMessageCountProgressionAlgorithms {
    @Bean
    public FactoryBean<?> spatMessageCountProgressionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatMessageCountProgressionAlgorithmFactory.class);
        return factoryBean;
    }
}