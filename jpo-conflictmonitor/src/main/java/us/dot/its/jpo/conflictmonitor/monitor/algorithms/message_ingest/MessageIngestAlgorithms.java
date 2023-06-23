package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageIngestAlgorithms {
    @Bean
    public FactoryBean<?> messageIngestServiceLocatorFactoryBean() {
        var factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(MessageIngestAlgorithmFactory.class);
        return factoryBean;
    }
}
