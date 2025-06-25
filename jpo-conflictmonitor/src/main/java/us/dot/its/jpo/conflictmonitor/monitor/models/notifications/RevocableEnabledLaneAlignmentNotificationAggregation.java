package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import org.apache.commons.lang3.StringUtils;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEventAggregation;

public class RevocableEnabledLaneAlignmentNotificationAggregation
    extends NotificationAggregation<RevocableEnabledLaneAlignmentEventAggregation> {

    public RevocableEnabledLaneAlignmentNotificationAggregation() {
        super("RevocableEnabledLaneAlignmentNotificationAggregation");
    }

    @Override
    public String getUniqueId() {

        return String.format("%s_%s_%s_%s_%s",
                this.getNotificationType(),
                eventAggregation.getRoadRegulatorID(),
                eventAggregation.getIntersectionID(),
                StringUtils.join(eventAggregation.getRevocableLaneList(), "-"),
                StringUtils.join(eventAggregation.getEnabledLaneList(), "-")
        );
    }
}
