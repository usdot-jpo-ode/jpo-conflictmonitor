package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUpdateResult;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ConfigException extends Exception {
    private ConfigUpdateResult<?> result;

    public ConfigException(ConfigUpdateResult<?> result, Throwable cause) {
        super(cause);
        this.result = result;
    }
}
