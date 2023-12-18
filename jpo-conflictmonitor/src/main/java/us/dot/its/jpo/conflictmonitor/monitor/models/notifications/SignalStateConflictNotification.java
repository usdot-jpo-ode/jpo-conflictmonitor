package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;


public class SignalStateConflictNotification extends Notification {
    public SignalStateConflictNotification() {
        super("SignalStateConflictNotification");
    }

    @Getter private SignalStateConflictEvent event;

    public void setEvent(SignalStateConflictEvent event){
        if(event != null){
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s", 
            this.getNotificationType(),
            event.getFirstConflictingSignalGroup(),
            event.getSecondConflictingSignalGroup(),
            event.getIntersectionID(),
            event.getRoadRegulatorID()
        );
    }
}