package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent;

@Getter
@Setter
public class EventStateProgressionNotification extends Notification {

    public EventStateProgressionNotification() {
        super("EventStateProgressionNotification");
    }

    EventStateProgressionEvent event;

    public void setEvent(EventStateProgressionEvent event) {
        if (event != null) {
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
            this.setNotificationHeading("Event State Progression");
            this.setNotificationText("An illegal SPaT transition was detected");
        }
    }

    @Override
    public String getUniqueId() {
        if (event != null) {
            return String.format("%s_%s_%s_%s_%s",
                    this.getNotificationType(),
                    event.getSource(),
                    event.getRoadRegulatorID(),
                    event.getIntersectionID(),
                    event.getSignalGroupID());
        } else {
            return this.getNotificationType();
        }
    }
}
