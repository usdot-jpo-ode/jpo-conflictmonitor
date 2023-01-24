package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import java.time.ZonedDateTime;

import us.dot.its.jpo.conflictmonitor.monitor.models.NotificationSource;

public class Notification {
    private long notificationGeneratedAt;
    private NotificationSource source;
    private String notificationType;
    private String notitificationHeading;
    private String notificationText;
    private int roadRegulatorID;
    private int intersectionID;

    public Notification(){
        setNotificationGeneratedAt(ZonedDateTime.now().toInstant().toEpochMilli());
    }

    public long getNotificationGeneratedAt() {
        return notificationGeneratedAt;
    }

    public void setNotificationGeneratedAt(long notificationGeneratedAt) {
        this.notificationGeneratedAt = notificationGeneratedAt;
    }

    public NotificationSource getSource() {
        return source;
    }

    public void setSource(NotificationSource source) {
        this.source = source;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotitificationHeading() {
        return notitificationHeading;
    }

    public void setNotitificationHeading(String notitificationHeading) {
        this.notitificationHeading = notitificationHeading;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
    
    public int getRoadRegulatorID() {
        return roadRegulatorID;
    }

    public void setRoadRegulatorID(int roadRegulatorID) {
        this.roadRegulatorID = roadRegulatorID;
    }

    public int getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(int intersectionID) {
        this.intersectionID = intersectionID;
    }

}
