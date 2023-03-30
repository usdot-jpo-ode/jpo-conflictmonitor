package us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationAlgorithms {
    @Bean
    public FactoryBean<?> notificationServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(NotificationAlgorithmFactory.class);
        return factoryBean;
    }
}