package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AggregationAlgorithms {

    @Bean
    FactoryBean<?> spatMinimumDataServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SpatMinimumDataAggregationAlgorithmFactory.class);
        return factoryBean;
    }

    @Bean
    FactoryBean<?> mapMinimumDataServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapMinimumDataAggregationAlgorithmFactory.class);
        return factoryBean;
    }
}
