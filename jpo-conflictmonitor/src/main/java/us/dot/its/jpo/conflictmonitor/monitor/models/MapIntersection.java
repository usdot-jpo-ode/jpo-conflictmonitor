package us.dot.its.jpo.conflictmonitor.monitor.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.io.WKTWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometry;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Connection;
import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectsToList;
import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;

public class MapIntersection {

    private static final Logger logger = LoggerFactory.getLogger(MapIntersection.class);

    private J2735IntersectionGeometry intersectionGeometry;
    @Getter private ArrayList<LaneConnection> laneConnections = new ArrayList<>();

    public MapIntersection(J2735IntersectionGeometry intersectionGeometry) {
        this.intersectionGeometry = intersectionGeometry;
        this.laneConnections = new ArrayList<>();

        extractConnections();
        getPathAsLatLong();
    }

    public void extractConnections() {
        List<J2735GenericLane> lanes = this.intersectionGeometry.getLaneSet().getLaneSet();
        OdePosition3D reference = this.intersectionGeometry.getRefPoint();

        Map<Integer, J2735GenericLane> laneLookup = new HashMap<>();
        for (J2735GenericLane lane : lanes) {
            if (lane.getLaneID() == null) {
                logger.error("Lane ID is missing");
                continue;
            }
            laneLookup.put(lane.getLaneID(), lane);
        }

        for (J2735GenericLane lane : lanes) {
            J2735ConnectsToList connectsTo = lane.getConnectsTo();
            if (connectsTo != null) {
                List<J2735Connection> connections = connectsTo.getConnectsTo();

                for (J2735Connection connection : connections) {

                    int connectingLaneID = connection.getConnectingLane().getLane();
                    int signalGroup = 0;
                    if (connection.getSignalGroup() != null) {
                        signalGroup = connection.getSignalGroup();
                    }
                    if (!laneLookup.containsKey(connectingLaneID)) {
                        logger.error("Could not find connecting lane with ID: {}", connectingLaneID);
                        continue;
                    }
                    this.laneConnections.add(
                            new LaneConnection(reference, lane, laneLookup.get(connectingLaneID), signalGroup, 25));
                }
            }

        }
    }

    

    public String getConnectionsAsWKT() {
        WKTWriter writer = new WKTWriter(2);
        String wtkOut = "wtk\n";
        writer.setFormatted(true);
        for (LaneConnection connection: this.laneConnections) {
            wtkOut += "\"" + writer.writeFormatted(connection.getIngressPath()) + "\"\n";
            wtkOut += "\"" + writer.writeFormatted(connection.getConnectingPath()) + "\"\n";
            wtkOut += "\"" + writer.writeFormatted(connection.getEgressPath()) + "\"\n";

        }

        return wtkOut;
    }

    public void getPathAsLatLong(){
        for(LaneConnection connection: this.laneConnections){
            connection.printLineStringLatLongAsCSV(connection.getIngressPath());
            break;
        }
    }

}
