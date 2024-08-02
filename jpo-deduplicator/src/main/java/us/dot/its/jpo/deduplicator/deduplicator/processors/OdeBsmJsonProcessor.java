package us.dot.its.jpo.deduplicator.deduplicator.processors;


import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.geotools.referencing.GeodeticCalculator;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;

public class OdeBsmJsonProcessor extends DeduplicationProcessor<OdeBsmData>{

    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    DeduplicatorProperties props;
    GeodeticCalculator calculator;

    public OdeBsmJsonProcessor(String storeName, DeduplicatorProperties props){
        this.storeName = storeName;
        this.props = props;
        calculator = new GeodeticCalculator();
    }


    @Override
    public Instant getMessageTime(OdeBsmData message) {
        try {
            String time = ((OdeBsmMetadata)message.getMetadata()).getOdeReceivedAt();
            return Instant.from(formatter.parse(time));
        } catch (Exception e) {
            System.out.println("Failed to Parse Time");
            return Instant.ofEpochMilli(0);
        }
    }

    @Override
    public boolean isDuplicate(OdeBsmData lastMessage, OdeBsmData newMessage) {
        Instant newValueTime = getMessageTime(newMessage);
        Instant oldValueTime = getMessageTime(lastMessage);

        // If the messages are more than a certain time apart, forward the new message on
        if(newValueTime.minus(Duration.ofMillis(props.getOdeBsmMaximumTimeDelta())).isAfter(oldValueTime)){
            return false;  
        }

        J2735BsmCoreData oldCore = ((J2735Bsm)lastMessage.getPayload().getData()).getCoreData();
        J2735BsmCoreData newCore = ((J2735Bsm)newMessage.getPayload().getData()).getCoreData();


        // If the Vehicle is moving, forward the message on
        if(newCore.getSpeed().doubleValue() > props.getOdeBsmAlwaysIncludeAtSpeed()){
            return false; 
        }


        double distance = calculateGeodeticDistance(
            newCore.getPosition().getLatitude().doubleValue(),
            newCore.getPosition().getLongitude().doubleValue(),
            oldCore.getPosition().getLatitude().doubleValue(),
            oldCore.getPosition().getLongitude().doubleValue()
        );

        // If the position delta between the messages is suitable large, forward the message on
        if(distance > props.getOdeBsmMaximumPositionDelta()){
            return false;
        }

        return true;
    }

    public double calculateGeodeticDistance(double lat1, double lon1, double lat2, double lon2) {
        calculator.setStartingGeographicPoint(lon1, lat1);
        calculator.setDestinationGeographicPoint(lon2, lat2);
        return calculator.getOrthodromicDistance();
    }
}
