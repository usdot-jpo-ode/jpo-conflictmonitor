package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionOfTravelAssessmentAlgorithms {
    @Bean
    public FactoryBean<?> cotaServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(ConnectionOfTravelAssessmentAlgorithmFactory.class);
        return factoryBean;
    }
}
