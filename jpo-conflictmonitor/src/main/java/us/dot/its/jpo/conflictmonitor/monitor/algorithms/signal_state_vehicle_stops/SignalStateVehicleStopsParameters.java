package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;


@Component
@PropertySource("classpath:signalStateVehicleStops-${signal.state.vehicle.stops.properties}.properties")
public class SignalStateVehicleStopsParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;
    int spatBsmMatchWindowMillis;
    double stopSpeedThreshold;


   
    public double getStopSpeedThreshold() {
        return stopSpeedThreshold;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public int getSpatBsmMatchWindowMillis() {
        return spatBsmMatchWindowMillis;
    }

    @Value("${signal.state.vehicle.stops.spatBsmMatchWindowMillis}")
    public void setSpatBsmMathWindowMillis(int spatBsmMatchWindowMillis) {
        this.spatBsmMatchWindowMillis = spatBsmMatchWindowMillis;
    }

    @Value("${signal.state.vehicle.stops.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Value("${signal.state.vehicle.stops.stopSpeedThreshold}")
    public void setStopSpeedThreshold(double stopSpeedThreshold) {
        this.stopSpeedThreshold = stopSpeedThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignalStateVehicleStopsParameters)) {
            return false;
        }
        SignalStateVehicleStopsParameters signalStateVehicleStopsParamters = (SignalStateVehicleStopsParameters) o;
        return debug == signalStateVehicleStopsParamters.debug;
    }

    @Override
    public int hashCode() {
        return Objects.hash(debug);
    }


    @Override
    public String toString() {
        return "{" +
            ", debug='" + isDebug() + "'" +
            "}";
    }
}



