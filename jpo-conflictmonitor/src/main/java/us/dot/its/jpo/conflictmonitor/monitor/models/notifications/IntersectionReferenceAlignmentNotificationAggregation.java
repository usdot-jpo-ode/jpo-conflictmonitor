package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEventAggregation;

public class IntersectionReferenceAlignmentNotificationAggregation
    extends NotificationAggregation<IntersectionReferenceAlignmentEventAggregation> {

    public IntersectionReferenceAlignmentNotificationAggregation() {
        super("IntersectionReferenceAlignmentNotificationAggregation");
    }


    @Override
    public String getUniqueId() {
        return String.format("%s_%s",
                this.getNotificationType(),
                eventAggregation.getSource()
        );
    }
}
