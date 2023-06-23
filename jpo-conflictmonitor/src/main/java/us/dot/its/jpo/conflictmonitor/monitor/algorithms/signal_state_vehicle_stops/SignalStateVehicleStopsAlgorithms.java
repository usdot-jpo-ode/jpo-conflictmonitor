package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignalStateVehicleStopsAlgorithms {

    @Bean
    public FactoryBean<?> ssvsServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SignalStateVehicleStopsAlgorithmFactory.class);
        return factoryBean;
    }   
}


