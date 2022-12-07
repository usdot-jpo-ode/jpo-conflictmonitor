package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapSpatMessageAssessmentAlgorithms {

    @Bean
    public FactoryBean<?> msmaServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MapSpatMessageAssessmentAlgorithmFactory.class);
        return factoryBean;
    }
    
}