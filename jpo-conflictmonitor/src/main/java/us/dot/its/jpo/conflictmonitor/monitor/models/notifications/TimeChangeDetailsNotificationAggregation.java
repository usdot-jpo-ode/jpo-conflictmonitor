package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEventAggregation;

public class TimeChangeDetailsNotificationAggregation
    extends NotificationAggregation<TimeChangeDetailsEventAggregation> {

    public TimeChangeDetailsNotificationAggregation() {
        super("TimeChangeDetailsNotificationAggregation");
    }

    @Override
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s_%s_%s_%s",
                this.getNotificationType(),
                eventAggregation.getRoadRegulatorID(),
                eventAggregation.getIntersectionID(),
                eventAggregation.getSignalGroupID(),
                eventAggregation.getTimeMarkTypeA(),
                eventAggregation.getTimeMarkTypeB(),
                eventAggregation.getEventStateA(),
                eventAggregation.getEventStateB()
        );
    }
}
