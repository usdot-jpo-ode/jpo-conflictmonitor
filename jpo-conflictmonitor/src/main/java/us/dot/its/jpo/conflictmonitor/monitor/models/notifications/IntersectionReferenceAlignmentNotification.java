package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;



public class IntersectionReferenceAlignmentNotification extends Notification {
    public IntersectionReferenceAlignmentNotification() {
        super("IntersectionReferenceAlignmentNotification");
    }

    @Getter @Setter private IntersectionReferenceAlignmentEvent event;

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s", 
            this.getNotificationType(), 
            event.getSourceID(),
            event.getEventType(),
            event.getSourceID()
            );
    }
}