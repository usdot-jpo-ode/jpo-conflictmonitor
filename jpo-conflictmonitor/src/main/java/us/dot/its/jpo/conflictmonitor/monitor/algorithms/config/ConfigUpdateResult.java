package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.Config;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigUpdateResult<T> {

    private Result result;
    private String message;
    private T oldValue;
    private T newValue;

    public boolean getSuccess() {
        return result != null && result.getSuccess();
    }

    public enum Result {
        UPDATED(true),
        ADDED(true),
        REMOVED(true),
        ERROR(false);

        final boolean success;

        public boolean getSuccess() {
            return success;
        }

        Result(boolean success) {
            this.success = success;
        }
    }
}


