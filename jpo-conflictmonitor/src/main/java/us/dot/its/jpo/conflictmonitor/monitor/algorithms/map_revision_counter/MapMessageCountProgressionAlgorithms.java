package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapMessageCountProgressionAlgorithms {
    @Bean
    public FactoryBean<?> mapMessageCountProgressionServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapMessageCountProgressionAlgorithmFactory.class);
        return factoryBean;
    }
}