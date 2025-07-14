package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public abstract class NotificationAggregation<TAggEvent extends EventAggregation> extends Notification {

    protected TAggEvent eventAggregation;
    protected String key;

    public void setEventAggregation(TAggEvent eventAggregation) {
        if (eventAggregation != null) {
            this.eventAggregation = eventAggregation;
            this.setIntersectionID(eventAggregation.getIntersectionID());
            this.setRoadRegulatorID(eventAggregation.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    public NotificationAggregation(String eventType) {
        super(eventType);
    }


}
