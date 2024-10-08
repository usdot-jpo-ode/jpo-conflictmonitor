

package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;

public class TimeChangeDetailsNotification extends Notification {
    public TimeChangeDetailsNotification() {
        super("TimeChangeDetailsNotification");
    }

    @Getter private TimeChangeDetailsEvent event;

    public void setEvent(TimeChangeDetailsEvent event){
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
        return String.format("%s_%s_%s_%s", 
            this.getNotificationType(),
            event.getRoadRegulatorID(),
            event.getIntersectionID(),
            event.getSignalGroup()          
        );
    }
}