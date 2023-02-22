

package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmTimeChangeDetailsNotification")
public class TimeChangeDetailsNotification extends Notification {
    public TimeChangeDetailsNotification() {
        super("TimeChangeDetailsNotification");
    }

    @Getter @Setter private TimeChangeDetailsEvent event;

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s", 
            this.getNotificationType(),
            event.getRoadRegulatorID(),
            event.getSignalGroup()          
        );
    }
}