package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_reference_alignment_notification;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntersectionReferenceAlignmentNotificationAlgorithms {

    @Bean
    public FactoryBean<?> iranServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(IntersectionReferenceAlignmentNotificationAlgorithmFactory.class);
        return factoryBean;
    }
    
}