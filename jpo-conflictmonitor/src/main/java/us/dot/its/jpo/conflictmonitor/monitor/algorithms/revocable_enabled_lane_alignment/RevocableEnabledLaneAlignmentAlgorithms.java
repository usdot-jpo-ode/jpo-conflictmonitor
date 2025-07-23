package us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RevocableEnabledLaneAlignmentAlgorithms {
    @Bean
    public FactoryBean<?> revocableEnabledLaneAlignmentAlgorithmServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(RevocableEnabledLaneAlignmentAlgorithmFactory.class);
        return factoryBean;
    }
}
