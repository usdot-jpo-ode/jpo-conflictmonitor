package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StopLineStopAssessmentAlgorithms {
    @Bean
    public FactoryBean<?> slsaServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(StopLineStopAssessmentAlgorithmFactory.class);
        return factoryBean;
    }
}