package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StopLinePassageAlgorithms {
    @Bean
    public FactoryBean<?> stopLinePassageServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(StopLinePassageAlgorithmFactory.class);
        return factoryBean;
    }
}