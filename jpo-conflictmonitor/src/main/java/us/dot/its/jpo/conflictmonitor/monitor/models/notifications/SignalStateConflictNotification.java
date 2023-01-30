package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;



public class SignalStateConflictNotification extends Notification {
    public SignalStateConflictNotification() {
        super("SignalStateConflictNotification");
    }

    @Getter @Setter private SignalStateConflictEvent event;

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s_%s", 
            this.getNotificationType(),
            event.getFirstConflictingSignalGroup(),
            event.getSecondConflictingSignalGroup(),
            event.getIntersectionID(),
            event.getRoadRegulatorID(),
            event.getEventType()
        );
    }
}