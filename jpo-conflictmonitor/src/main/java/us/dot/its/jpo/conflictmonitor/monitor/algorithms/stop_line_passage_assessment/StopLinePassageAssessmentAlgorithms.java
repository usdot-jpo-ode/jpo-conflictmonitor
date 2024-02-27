package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StopLinePassageAssessmentAlgorithms {
    @Bean
    public FactoryBean<?> sseaaServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(StopLinePassageAssessmentAlgorithmFactory.class);
        return factoryBean;
    }
}