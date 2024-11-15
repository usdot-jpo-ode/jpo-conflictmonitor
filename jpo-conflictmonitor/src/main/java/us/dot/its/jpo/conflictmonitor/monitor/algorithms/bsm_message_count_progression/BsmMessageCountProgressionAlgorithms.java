package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BsmMessageCountProgressionAlgorithms {
    @Bean
    public FactoryBean<?> bsmMessageCountProgressionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(BsmMessageCountProgressionAlgorithmFactory.class);
        return factoryBean;
    }
}