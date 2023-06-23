package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LaneDirectionOfTravelAssessmentAlgorithms {

    @Bean
    public FactoryBean<?> ldotaServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(LaneDirectionOfTravelAssessmentAlgorithmFactory.class);
        return factoryBean;
    }
    
}