package us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssessmentAlgorithms {
    @Bean
    public FactoryBean<?> assessmentServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(AssessmentAlgorithmFactory.class);
        return factoryBean;
    }
}