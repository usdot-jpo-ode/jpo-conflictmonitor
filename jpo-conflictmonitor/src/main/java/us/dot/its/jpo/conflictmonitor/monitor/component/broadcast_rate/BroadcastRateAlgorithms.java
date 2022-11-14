package us.dot.its.jpo.conflictmonitor.monitor.component.broadcast_rate;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BroadcastRateAlgorithms {
    
    @Bean("mapBroadcastRateAlgorithmFactory")
    public FactoryBean<?> serviceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapBroadcastRateAlgorithmFactory.class);
        return factoryBean;
    }

}
