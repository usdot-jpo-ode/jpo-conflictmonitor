package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapRevisionCounterAlgorithms {
    @Bean
    public FactoryBean<?> mapRevisionCounterServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapRevisionCounterAlgorithmFactory.class);
        return factoryBean;
    }
}