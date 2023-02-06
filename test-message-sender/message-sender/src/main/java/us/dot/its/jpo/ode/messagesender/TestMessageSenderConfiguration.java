package us.dot.its.jpo.ode.messagesender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
public class TestMessageSenderConfiguration {
    
    String mapboxTileEndpoint;

    @Value("${mapbox.tile.endpoint}")
    public void setMapboxTileEndpoint(String mapboxTileEndpoint) {
        this.mapboxTileEndpoint = mapboxTileEndpoint;
    }

    public String getMapboxTileEndpoint() {
        return mapboxTileEndpoint;
    }
}
