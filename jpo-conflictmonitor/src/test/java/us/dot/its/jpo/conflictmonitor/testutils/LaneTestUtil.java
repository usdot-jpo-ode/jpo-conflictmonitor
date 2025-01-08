package us.dot.its.jpo.conflictmonitor.testutils;

import java.math.BigDecimal;
import java.util.Arrays;

import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectingLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735Connection;
import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectsToList;
import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometry;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneList;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeListXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;

/**
 * Helper methods to generate lane test data.
 */
public class LaneTestUtil {

    public static J2735GenericLane getGenericLane(Integer laneId, double[][] offsets) {
        var lane = new J2735GenericLane();
        lane.setLaneID(laneId);
        var nodeList = new J2735NodeListXY();
        lane.setNodeList(nodeList);
        
        J2735NodeXY[] nodes = new J2735NodeXY[offsets.length];
        int index = 0;
        for (double[] point : offsets) {
            var node = new J2735NodeXY();
            var delta = new J2735NodeOffsetPointXY();
            var nodeXY = new J2735Node_XY();
            nodeXY.setX(BigDecimal.valueOf(point[0]));
            nodeXY.setY(BigDecimal.valueOf(point[1]));
            delta.setNodeXY1(nodeXY);
            node.setDelta(delta);
            nodes[index] = node;
            index +=1;
        }

        nodeList.setNodes(nodes);
        
        return lane;
    }

    public static J2735GenericLane getGenericLane(Integer laneId, double[][] offsets, 
            Integer signalGroup, Integer[] connectsTo) {

        var lane = getGenericLane(laneId, offsets);
        if (connectsTo != null) {
            var connectsToList = new J2735ConnectsToList();
            for (Integer connectToLaneId : connectsTo) {
                if (connectToLaneId == null) {
                    continue;
                }
                var connection = new J2735Connection();
                connection.setSignalGroup(signalGroup);
                var connectingLane = new J2735ConnectingLane();
                connectingLane.setLane(connectToLaneId);
                connection.setConnectingLane(connectingLane);
                connectsToList.getConnectsTo().add(connection);
            }
            lane.setConnectsTo(connectsToList);
        }
        return lane;
    }

    public static J2735IntersectionGeometry intersectionGeometry(
            OdePosition3D refPoint, J2735GenericLane... lanes) {
        var intersectionGeometry = new J2735IntersectionGeometry();
        intersectionGeometry.setRefPoint(refPoint);
        var laneList = new J2735LaneList();
        intersectionGeometry.setLaneSet(laneList);
        laneList.getLaneSet().addAll(Arrays.asList(lanes));
        return intersectionGeometry;
    }

    public static OdePosition3D getRefPoint(double lon, double lat, double alt) {
        var refPoint = new OdePosition3D();
        refPoint.setLatitude(BigDecimal.valueOf(lat));
        refPoint.setLongitude(BigDecimal.valueOf(lon));
        refPoint.setElevation(BigDecimal.valueOf(alt));
        return refPoint;
    }
    
}
