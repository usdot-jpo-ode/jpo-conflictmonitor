package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateParameters;

@Configuration
@PropertySource("classpath:spatBroadcastRate-${spat.broadcast.rate.properties}.properties")
public class SpatBroadcastRateParameters
    extends BroadcastRateParameters {}
