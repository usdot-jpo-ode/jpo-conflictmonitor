package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_requirement;

import java.util.List;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;

@Data
@Generated
public abstract class MinimumDataEvent {
    private boolean cti4501Conformant;
    private List<ProcessedValidationMessage> validationMessages;
}
