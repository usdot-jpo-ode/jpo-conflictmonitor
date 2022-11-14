package us.dot.its.jpo.conflictmonitor.monitor.component.broadcast_rate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BroadcastRateConfigurations {

    /**
     * @return Default MAP broadcast rate parameters per the requirements with 10 second wide, 5 second hopping window
     */
    @Bean("defaultMapBroadcastRateParameters")
    public BroadcastRateParameters defaultMapBroadcastRateParamaters() {
        var params = new BroadcastRateParameters();
        params.setInputTopicName("topic.OdeMapJson");
        params.setOutputTopicName("topic.ConflictMonitor.MapBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setLowerBound(9);
        params.setUpperBound(11);
        return params;
    }

    /**
     * @return Alernate parameters with a tumbling window (output interval same as size of window)
     */
    @Bean("alternateMapBroadcastRateParameters")
    public BroadcastRateParameters alternateMapBroadcastRateParamaters() {
        var params = new BroadcastRateParameters();
        params.setInputTopicName("topic.OdeMapJson");
        params.setOutputTopicName("topic.ConflictMonitor.MapBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(10);
        params.setLowerBound(9);
        params.setUpperBound(11);
        return params;
    }

    @Bean("defaultSpatBroadcastRateParameters")
    public BroadcastRateParameters defaultSpatBroadcastRateParamaters() {
        var params = new BroadcastRateParameters();
        params.setInputTopicName("topic.OdeSpatJson");
        params.setOutputTopicName("topic.ConflictMonitor.SpatBroadcastRateEvents");
        params.setRollingPeriodSeconds(10);
        params.setOutputIntervalSeconds(5);
        params.setLowerBound(90);
        params.setUpperBound(110);
        return params;
    }
    
}
