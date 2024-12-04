package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEventAggregation;

public class SignalStateConflictNotificationAggregation
    extends NotificationAggregation<SignalStateConflictEventAggregation> {


    public SignalStateConflictNotificationAggregation() {
        super("SignalStateConflictNotificationAggregation");
    }


    @Override
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s_%s_%s",
                this.getNotificationType(),
                eventAggregation.getConflictingSignalGroupA(),
                eventAggregation.getEventStateA(),
                eventAggregation.getConflictingSignalGroupB(),
                eventAggregation.getEventStateB(),
                eventAggregation.getIntersectionID(),
                eventAggregation.getRoadRegulatorID()
        );
    }
}
