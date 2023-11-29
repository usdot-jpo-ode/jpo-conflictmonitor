package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StopLineStopAlgorithms {

    @Bean
    public FactoryBean<?> ssvsServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(StopLineStopAlgorithmFactory.class);
        return factoryBean;
    }   
}


