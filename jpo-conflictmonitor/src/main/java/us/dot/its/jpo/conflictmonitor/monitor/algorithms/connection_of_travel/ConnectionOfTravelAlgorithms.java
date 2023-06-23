package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionOfTravelAlgorithms {
    @Bean
    public FactoryBean<?> cotServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(ConnectionOfTravelAlgorithmFactory.class);
        return factoryBean;
    }
}
