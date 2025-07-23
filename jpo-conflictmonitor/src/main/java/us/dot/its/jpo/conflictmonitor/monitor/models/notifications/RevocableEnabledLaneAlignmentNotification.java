package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEvent;

@Getter
public class RevocableEnabledLaneAlignmentNotification extends Notification {

    public RevocableEnabledLaneAlignmentNotification() {
        super("RevocableEnabledLaneAlignmentNotification");
    }

    private RevocableEnabledLaneAlignmentEvent event;

    public void setEvent(RevocableEnabledLaneAlignmentEvent event) {
        if (event != null) {
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    @Override
    public String getUniqueId() {
        return String.format("%s_%s_%s",
                this.getNotificationType(), event.getRoadRegulatorID(), event.getIntersectionID());
    }
}
