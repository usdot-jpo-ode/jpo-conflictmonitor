package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignalStateEventAssessmentAlgorithms {
    @Bean
    public FactoryBean<?> sseaaServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SignalStateEventAssessmentAlgorithmFactory.class);
        return factoryBean;
    }
}