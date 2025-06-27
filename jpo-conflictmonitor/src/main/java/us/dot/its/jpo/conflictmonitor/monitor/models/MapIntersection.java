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

/**
 * Represents an intersection as defined in a MAP message, including its lanes and lane connections.
 * Provides methods to extract lane connections and output connection paths in WKT format.
 */
public class MapIntersection {

    /** Logger for MapIntersection operations. */
    private static final Logger logger = LoggerFactory.getLogger(MapIntersection.class);

    /** The intersection geometry as defined in the MAP message. */
    private J2735IntersectionGeometry intersectionGeometry;

    /** List of lane connections within the intersection. */
    @Getter private ArrayList<LaneConnection> laneConnections = new ArrayList<>();

    /**
     * Constructs a MapIntersection from the provided intersection geometry.
     *
     * @param intersectionGeometry the intersection geometry from the MAP message
     */
    public MapIntersection(J2735IntersectionGeometry intersectionGeometry) {
        this.intersectionGeometry = intersectionGeometry;
        this.laneConnections = new ArrayList<>();

        extractConnections();
        getPathAsLatLong();
    }

    /**
     * Extracts lane connections from the intersection geometry and populates the laneConnections list.
     * Each connection links an ingress lane to a connecting lane, with associated signal group.
     */
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

    /**
     * Returns the lane connections as a Well-Known Text (WKT) string.
     * Each connection's ingress, connecting, and egress paths are included.
     *
     * @return WKT representation of all lane connections
     */
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

    /**
     * Prints the latitude/longitude CSV of the first connection's ingress path.
     * This is primarily for debugging or demonstration purposes.
     */
    public void getPathAsLatLong(){
        for(LaneConnection connection: this.laneConnections){
            connection.printLineStringLatLongAsCSV(connection.getIngressPath());
            break;
        }
    }

}
