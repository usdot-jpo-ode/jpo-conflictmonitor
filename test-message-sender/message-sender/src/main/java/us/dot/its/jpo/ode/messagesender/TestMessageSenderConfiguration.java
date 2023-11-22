package us.dot.its.jpo.ode.messagesender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
public class TestMessageSenderConfiguration {
    
    String basemapUrl;
    String basemapAttribution;

    @Value("${basemap.url}")
    public void setBasemapUrl(String basemapUrl) {
        this.basemapUrl = basemapUrl;
    }

    public String getBasemapUrl() {
        return basemapUrl;
    }

    @Value("${basemap.attribution}")
    public void setBasemapAttribution(String basemapAttribution) {
        this.basemapAttribution = basemapAttribution;
    }

    public String getBasemapAttribution() {
        return basemapAttribution;
    }
}
