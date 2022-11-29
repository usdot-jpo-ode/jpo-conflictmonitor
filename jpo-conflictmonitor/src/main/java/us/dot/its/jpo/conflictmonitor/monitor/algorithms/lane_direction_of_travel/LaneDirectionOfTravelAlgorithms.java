package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LaneDirectionOfTravelAlgorithms {

    @Bean
    public FactoryBean<?> ldotServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(LaneDirectionOfTravelAlgorithmFactory.class);
        return factoryBean;
    }
    
}