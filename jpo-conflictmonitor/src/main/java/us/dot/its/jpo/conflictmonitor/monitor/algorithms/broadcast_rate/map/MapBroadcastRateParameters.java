package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateParameters;

@Configuration
@PropertySource("classpath:mapBroadcastRate-${map.broadcast.rate.properties}.properties")
public class MapBroadcastRateParameters
    extends BroadcastRateParameters {}
