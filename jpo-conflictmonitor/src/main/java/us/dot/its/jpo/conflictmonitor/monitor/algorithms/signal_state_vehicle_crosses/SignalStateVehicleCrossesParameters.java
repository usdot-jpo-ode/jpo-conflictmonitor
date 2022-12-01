package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:signalStateVehicleCrosses-${signal.state.vehicle.crosses.properties}.properties")
public class SignalStateVehicleCrossesParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;

    int spatBsmMatchWindowMillis;

    public boolean isDebug() {
        return this.debug;
    }

    @Value("${signal.state.vehicle.crosses.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getSpatBsmMatchWindowMillis() {
        return spatBsmMatchWindowMillis;
    }

    @Value("${signal.state.vehicle.crosses.spatBsmMatchWindowMillis}")
    public void setSpatBsmMathWindowMillis(int spatBsmMatchWindowMillis) {
        this.spatBsmMatchWindowMillis = spatBsmMatchWindowMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignalStateVehicleCrossesParameters)) {
            return false;
        }
        SignalStateVehicleCrossesParameters signalStateVehicleCrossesParameters = (SignalStateVehicleCrossesParameters) o;
        return debug == signalStateVehicleCrossesParameters.debug;
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
