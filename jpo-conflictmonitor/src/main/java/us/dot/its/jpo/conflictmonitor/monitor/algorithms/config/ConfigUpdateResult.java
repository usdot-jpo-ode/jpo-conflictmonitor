package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.Config;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConfigUpdateResult<T> {

    private Result result;
    private String message;
    private Config<T> oldValue;
    private Config<T> newValue;

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


