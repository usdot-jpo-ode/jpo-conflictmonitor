package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEventAggregation;

public class EventStateProgressionNotificationAggregation
        extends NotificationAggregation<EventStateProgressionEventAggregation> {

    public EventStateProgressionNotificationAggregation() {
        super("EventStateProgressionNotificationAggregation");
    }

    @Override
    public void setEventAggregation(EventStateProgressionEventAggregation eventAggregation) {
        super.setEventAggregation(eventAggregation);
        setNotificationHeading("Event State Progression Aggregation");
        setNotificationText("Illegal SPaT transitions were detected");
    }

    @Override
    public String getUniqueId() {
        if (eventAggregation != null) {
            return String.format("%s_%s_%s_%s_%s_%s_%s",
                    this.getNotificationType(),
                    eventAggregation.getSource(),
                    eventAggregation.getRoadRegulatorID(),
                    eventAggregation.getIntersectionID(),
                    eventAggregation.getSignalGroupID(),
                    eventAggregation.getEventStateA(),
                    eventAggregation.getEventStateB());
        } else {
            return this.getNotificationType();
        }
    }
}
