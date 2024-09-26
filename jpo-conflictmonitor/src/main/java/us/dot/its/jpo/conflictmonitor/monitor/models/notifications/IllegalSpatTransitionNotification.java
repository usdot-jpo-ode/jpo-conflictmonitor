package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IllegalSpatTransitionEvent;

@Getter
@Setter
public class IllegalSpatTransitionNotification extends Notification {

    public IllegalSpatTransitionNotification() {
        super("IllegalSpatTransitionNotification");
    }

    IllegalSpatTransitionEvent event;

    public void setEvent(IllegalSpatTransitionEvent event) {
        if (event != null) {
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
            this.setNotificationHeading("Illegal SPaT Transition");
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
                    event.getSignalGroup());
        } else {
            return this.getNotificationType();
        }
    }
}
