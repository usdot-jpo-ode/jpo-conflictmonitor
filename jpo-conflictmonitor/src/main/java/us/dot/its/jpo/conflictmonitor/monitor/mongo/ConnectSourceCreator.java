package us.dot.its.jpo.conflictmonitor.monitor.mongo;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;

@Service
public class ConnectSourceCreator {

    final static Logger logger = LoggerFactory.getLogger(ConnectSourceCreator.class);


    final ConflictMonitorProperties properties;
    final ConfigParameters configParams;

    @Autowired
    public ConnectSourceCreator(ConflictMonitorProperties properties, ConfigParameters configParams) {
        this.properties = properties;
        this.configParams = configParams;
         
    }


    public void createDefaultConfigConnector() {
        final var jsonParams = 
            configParams.getCreateDefaultConnectorJsonParams()
                .replace("{{DOCKER_HOST_IP}}", properties.getDockerHostIP())
                .replace("{{defaultTopicName}}", configParams.getDefaultTopicName())
                .replace("{{defaultCollectionName}}", configParams.getDefaultCollectionName());
        final var url = 
            String.format("%s/connectors/MongoSource.%s/config", 
                properties.getConnectURL(),
                configParams.getDefaultTopicName());
        createConfigConnector(url, jsonParams);
     }

     public void createIntersectionConfigConnector() {
        final var jsonParams = 
            configParams.getCreateIntersectionConnectorJsonParams()
                .replace("{{DOCKER_HOST_IP}}", properties.getDockerHostIP())
                .replace("{{intersectionTopicName}}", configParams.getIntersectionTopicName())
                .replace("{{intersectionCollectionName}}", configParams.getIntersectionCollectionName());
        final var url = 
            String.format("%s/connectors/MongoSource.%s/config", 
                properties.getConnectURL(),
                configParams.getIntersectionTopicName());
        createConfigConnector(url, jsonParams);
     }

     private void createConfigConnector(String url, String jsonParams) {
        logger.info("Connect params: {}", jsonParams);
        final var restTemplate = new RestTemplate();
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> request = new HttpEntity<>(jsonParams, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Succes creating connector: {}", response.getBody());
        } 
     }
    
}
