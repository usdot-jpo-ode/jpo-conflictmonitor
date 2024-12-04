package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEventAggregation;

public class SignalGroupAlignmentNotificationAggregation
    extends NotificationAggregation<SignalGroupAlignmentEventAggregation>{

    public SignalGroupAlignmentNotificationAggregation() {
        super("SignalGroupAlignmentNotificationAggregation");
    }


    @Override
    public String getUniqueId() {
        return String.format("%s_%s_%s",
                this.getNotificationType(),
                eventAggregation.getRoadRegulatorID(),
                eventAggregation.getIntersectionID()
        );
    }
}
