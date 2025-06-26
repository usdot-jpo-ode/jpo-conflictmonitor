package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

/**
 * LaneConnection represents a connection between an ingress and egress lane within an intersection.
 * It provides methods to compute the geometric connection and check for crossing with other connections.
 */
public class LaneConnection {
    
    /**
     * The ingress lane for this connection.
     * This is the lane where vehicles enter the intersection.
     */
    private Lane ingressLane;

    /**
     * The egress lane for this connection.
     * This is the lane where vehicles exit the intersection.
     */
    private Lane egressLane;

    /**
     * int representing the connectionId for this lane connection. Connection Ids are unique across the intersection 
     */
    private int connectionId;

    /**
     * An integer representing the signal group associated with this connection. The Signal group is selected from the MAP message based upon the ingress lane
     */
    private int signalGroup;

    /**
     * A LineString representing the geometric connection between the ingress and egress lanes.
     * This is calculated based on the start of the ingress lane and the end of the egress lane.
     */
    private LineString connectingLineString;

    /**
     * Constructs a LaneConnection with the specified ingress lane, egress lane, connection ID, and signal group.
     *
     * @param ingressLane  the ingress Lane object
     * @param egressLane   the egress Lane object
     * @param connectionId the unique identifier for this connection
     * @param signalGroup  the signal group associated with this connection
     */
    public LaneConnection(Lane ingressLane, Lane egressLane, int connectionId, int signalGroup){
        this.ingressLane = ingressLane;
        this.egressLane = egressLane;
        this.connectionId = connectionId;
        this.signalGroup = signalGroup;
        this.connectingLineString = calculateConnectingLineString(25);
    }

    /**
     * Computes the LineString representing the geometric connection between the ingress and egress lanes.
     * Currently, this method creates a straight line between the start of the ingress lane and the end of the egress lane.
     *
     * @param numIntermediatePoints the number of intermediate points to use in the connection (currently unused)
     * @return a LineString representing the connection
     */
    public LineString calculateConnectingLineString(int numIntermediatePoints){
        int egressPointCount = egressLane.getPoints().getNumPoints();
        Point startPoint = ingressLane.getPoints().getPointN(0);
        Point leadInPoint = ingressLane.getPoints().getPointN(1);

        Point endPoint = egressLane.getPoints().getPointN(egressPointCount-1);
        Point leadOutPoint = egressLane.getPoints().getPointN(egressPointCount-2);

        Coordinate[] connection = new Coordinate[2];
        connection[0] = new Coordinate(startPoint.getX(), startPoint.getY());
        connection[1] = new Coordinate(endPoint.getX(), endPoint.getY());

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(connection);
        return new LineString(sequence, this.ingressLane.getGeometryFactory());
    }

    /**
     * Determines if this LaneConnection crosses another LaneConnection.
     *
     * @param otherConnection the other LaneConnection to check against
     * @return true if the connections cross, false otherwise
     */
    public boolean crosses(LaneConnection otherConnection){
        return this.connectingLineString.crosses(otherConnection.getConnectingLineString());
    }

    /**
     * Gets the LineString representing the connection.
     *
     * @return the connecting LineString
     */
    public LineString getConnectingLineString() {
        return connectingLineString;
    }

    /**
     * Sets the LineString representing the connection.
     *
     * @param connectingLineString the LineString to set
     */
    public void setConnectingLineString(LineString connectingLineString) {
        this.connectingLineString = connectingLineString;
    }

    /**
     * Gets the ingress Lane.
     *
     * @return the ingress Lane
     */
    public Lane getIngressLane() {
        return ingressLane;
    }

    /**
     * Sets the ingress Lane.
     *
     * @param ingressLane the ingress Lane to set
     */
    public void setIngressLane(Lane ingressLane) {
        this.ingressLane = ingressLane;
    }
    
    /**
     * Gets the egress Lane.
     *
     * @return the egress Lane
     */
    public Lane getEgressLane() {
        return egressLane;
    }

    /**
     * Sets the egress Lane.
     *
     * @param egressLane the egress Lane to set
     */
    public void setEgressLane(Lane egressLane) {
        this.egressLane = egressLane;
    }
    
    /**
     * Gets the connection ID.
     *
     * @return the connection ID
     */
    public int getConnectionId() {
        return connectionId;
    }

    /**
     * Sets the connection ID.
     *
     * @param connectionId the connection ID to set
     */
    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * Gets the signal group.
     *
     * @return the signal group
     */
    public int getSignalGroup() {
        return signalGroup;
    }

    /**
     * Sets the signal group.
     *
     * @param signalGroup the signal group to set
     */
    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

}
