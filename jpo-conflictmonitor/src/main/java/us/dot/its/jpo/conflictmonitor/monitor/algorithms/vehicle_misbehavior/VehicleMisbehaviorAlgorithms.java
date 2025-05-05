package us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VehicleMisbehaviorAlgorithms {
    @Bean
    public FactoryBean<?> vmServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(VehicleMisbehaviorAlgorithmFactory.class);
        return factoryBean;
    }
}
