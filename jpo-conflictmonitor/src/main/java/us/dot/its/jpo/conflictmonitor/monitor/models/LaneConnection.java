package us.dot.its.jpo.conflictmonitor.monitor.models;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public class LaneConnection {
    private OdePosition3D referencePoint;

    private J2735GenericLane ingress;
    private J2735GenericLane egress;

    private LineString ingressPath;
    private LineString connectingPath;
    private LineString egressPath;

    private GeometryFactory geometryFactory;
    private int signalGroup;
    private int interpolationPoints;

    public LaneConnection(OdePosition3D referencePoint, J2735GenericLane ingress, J2735GenericLane egress, int signalGroup) {
        this(referencePoint, ingress, egress, signalGroup, 10);
    }

    public LaneConnection(OdePosition3D referencePoint, J2735GenericLane ingress, J2735GenericLane egress, int signalGroup, int interpolationPoints) {
        this.referencePoint = referencePoint;
        this.ingress = ingress;
        this.egress = egress;
        this.signalGroup = signalGroup;
        this.interpolationPoints = interpolationPoints;

        this.geometryFactory = JTSFactoryFinder.getGeometryFactory();
        this.ingressPath = laneToLineString(ingress);
        this.egressPath = laneToLineString(egress);

        alignInputLanes();
        getConnectingLineString();


    }

    // Adjusts the Input lanes such that sequential points go in the direction of
    // vehicle travel
    public void alignInputLanes() {
        Coordinate[] startCoordinates = this.ingressPath.getCoordinates();
        Coordinate[] endCoordinates = this.egressPath.getCoordinates();

        // Pick the coordinates for both paths that have the shortest distance to one
        // another.
        int startEnd[][] = {
                { 0, 0 },
                { startCoordinates.length - 1, 0 },
                { 0, endCoordinates.length - 1 },
                { startCoordinates.length - 1, endCoordinates.length - 1 },
        };

        double minDistance = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < startEnd.length; i++) {
            Coordinate a = startCoordinates[startEnd[i][0]];
            Coordinate b = endCoordinates[startEnd[i][1]];
            double distance = getDistance(a, b);
            if (distance < minDistance) {
                minDistance = distance;
                index = i;
            }
        }

        // Reverse the Start and End sequences as needed to make all points face forward.

        if (startEnd[index][0] == 0) {
            this.ingressPath = this.ingressPath.reverse();
        }

        if (startEnd[index][1] != 0) {
            this.egressPath = this.egressPath.reverse();
        }

    }

    public double clamp(double value, double threshold) {
        return Math.max(Math.min(value, threshold), -threshold);
    }

    public void getConnectingLineString() {
        Coordinate[] startCoordinates = ingressPath.getCoordinates();
        Coordinate[] endCoordinates = egressPath.getCoordinates();

        Coordinate leadInPoint = startCoordinates[startCoordinates.length - 2];
        Coordinate startPoint = startCoordinates[startCoordinates.length - 1];

        Coordinate endPoint = endCoordinates[0];
        Coordinate leadOutPoint = endCoordinates[1];

        // Clamp the slope of the spline based upon how far it needs to go. This bounds
        // each spline and prevents splines from folding back on themselves.
        // Currently the L1 norm is being used, however both L1 and L2 produce
        // acceptable results.
        // Increasing the clamp value increases how much each interpolation can curve.
        // However if the threshold is set too high, the curves will fold back on
        // themselves.

        // double segmentLength = Math.sqrt(Math.pow(startPoint.x - endPoint.x,2) +
        // Math.pow(startPoint.y - endPoint.y,2)); //L2 Norm
        double segmentLength = Math.abs((startPoint.x - endPoint.x)) + Math.abs((startPoint.y - endPoint.y)); // L1 Norm

        double xInSlope = clamp((startPoint.x - leadInPoint.x), segmentLength);
        double yInSlope = clamp((startPoint.y - leadInPoint.y), segmentLength);
        double xOutSlope = clamp((leadOutPoint.x - endPoint.x), segmentLength);
        double yOutSlope = clamp((leadOutPoint.y - endPoint.y), segmentLength);

        double[][] equations = {
                { 1, 1, 1, 1 }, // equals startPoint (x,y)
                { 1, 2, 4, 8 }, // equals endPoint (x,y)
                { 0, 1, 2, 3 }, // equals inSlope (x,y)
                { 0, 1, 4, 12 }, // equals outSlope (x,y)
        };

        double[][] solutions = {
                { startPoint.x, startPoint.y },
                { endPoint.x, endPoint.y },
                { xInSlope, yInSlope },
                { xOutSlope, yOutSlope }
        };

        RealMatrix equationsMatrix = new Array2DRowRealMatrix(equations);
        RealMatrix solutionMatrix = new Array2DRowRealMatrix(solutions);
        DecompositionSolver solver = new LUDecomposition(equationsMatrix).getSolver();
        RealMatrix solution = solver.solve(solutionMatrix);

        Coordinate[] connection = new Coordinate[2 + this.interpolationPoints];
        connection[0] = startCoordinates[startCoordinates.length - 1];
        connection[this.interpolationPoints + 1] = endCoordinates[0];

        for (int i = 0; i < interpolationPoints; i++) {
            connection[i + 1] = evaluatePoint(solution, 1 + (i / (float) (interpolationPoints + 1.0)));
        }

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(connection);
        this.connectingPath = new LineString(sequence, geometryFactory);

    }

    // returns the coordinate found when evaluating the given cubic equations at
    // point u
    public Coordinate evaluatePoint(RealMatrix parameters, double u) {

        double x = parameters.getEntry(0, 0) + parameters.getEntry(1, 0) * u
                + parameters.getEntry(2, 0) * Math.pow(u, 2) + parameters.getEntry(3, 0) * Math.pow(u, 3);
        double y = parameters.getEntry(0, 1) + parameters.getEntry(1, 1) * u
                + parameters.getEntry(2, 1) * Math.pow(u, 2) + parameters.getEntry(3, 1) * Math.pow(u, 3);

        return new Coordinate(x, y);

    }

    public double getDistance(Coordinate A, Coordinate B) {
        return Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY() - B.getY(), 2));
    }

    public boolean detectConflict(LaneConnection otherConnection) {
        // Explicitly return that a connection is not in conflict with itself.
        if (this == otherConnection) {
            return false;
        }

        if (connectingPath.intersects(otherConnection.connectingPath)) {
            if (this.signalGroup == otherConnection.signalGroup) {
                System.out.println("Conflict Detected between " + this.ingress.getLaneID() + ","
                        + this.egress.getLaneID() + " and " + otherConnection.ingress.getLaneID() + ","
                        + otherConnection.egress.getLaneID());
                System.out.println("First Path");
                printConnectingPath();
                System.out.println("Second Path");
                otherConnection.printConnectingPath();

                return true;
            }
        }

        return false;

    }

    public LineString laneToLineString(J2735GenericLane lane) {
        List<J2735NodeXY> nodes = lane.getNodeList().getNodes().getNodes();

        Coordinate[] coordList = new Coordinate[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getDelta().getNodeLatLon() != null) {
                System.out.println("Reference Point Moved");
            }
            J2735NodeOffsetPointXY nodeOffset = nodes.get(i).getDelta();
            J2735Node_XY nodeXY = getNodeXY(nodeOffset);
            if (nodeXY == null) {
                continue;
            }

            double offsetX = nodeXY.getX().doubleValue();
            double offsetY = nodeXY.getY().doubleValue();

            if (i >= 1) {
                coordList[i] = new Coordinate(coordList[i - 1].getX() + offsetX, coordList[i - 1].getY() + offsetY);
            } else {
                coordList[i] = new Coordinate(offsetX, offsetY);
            }
        }

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(coordList);
        return new LineString(sequence, geometryFactory);
    }

    public J2735Node_XY getNodeXY(J2735NodeOffsetPointXY nodeOffset) {
        if (nodeOffset.getNodeXY1() != null) {
            return nodeOffset.getNodeXY1();
        } else if (nodeOffset.getNodeXY2() != null) {
            return nodeOffset.getNodeXY2();
        } else if (nodeOffset.getNodeXY3() != null) {
            return nodeOffset.getNodeXY3();
        } else if (nodeOffset.getNodeXY4() != null) {
            return nodeOffset.getNodeXY4();
        } else if (nodeOffset.getNodeXY5() != null) {
            return nodeOffset.getNodeXY5();
        } else if (nodeOffset.getNodeXY6() != null) {
            return nodeOffset.getNodeXY6();
        } else {
            return null;
        }
    }

    public void printLineStringAsCSV(LineString lstring) {
        Coordinate[] coords = lstring.getCoordinates();
        for (int i = 0; i < coords.length; i++) {
            System.out.println(coords[i].x + "," + coords[i].y);
        }
    }

    public void printConnectingPath() {
        printLineStringAsCSV(ingressPath);
        printLineStringAsCSV(connectingPath);
        printLineStringAsCSV(egressPath);
    }

    public void printLineStringLatLongAsCSV(LineString lstring){
        double referenceLongitude = this.referencePoint.getLongitude().doubleValue();
        double referenceLatitude = this.referencePoint.getLatitude().doubleValue();
        
        
        Coordinate[] coords = lstring.getCoordinates();
        for(Coordinate coord:coords){
            double[] longLat = CoordinateConversion.offsetMToLongLat(referenceLongitude, referenceLatitude, coord.x/100.0, coord.y/100.0);
            System.out.println(longLat[0] + ", " + longLat[1]);
        }
    }

    public J2735GenericLane getIngress() {
        return ingress;
    }

    public void setIngress(J2735GenericLane ingress) {
        this.ingress = ingress;
    }

    public J2735GenericLane getEgress() {
        return egress;
    }

    public void setEgress(J2735GenericLane egress) {
        this.egress = egress;
    }

    public LineString getConnectingPath() {
        return connectingPath;
    }

    public void setConnectingPath(LineString connectingPath) {
        this.connectingPath = connectingPath;
    }

    public LineString getIngressPath() {
        return ingressPath;
    }

    public void setIngressPath(LineString ingressPath) {
        this.ingressPath = ingressPath;
    }

    public LineString getEgressPath() {
        return egressPath;
    }

    public void setEgressPath(LineString egressPath) {
        this.egressPath = egressPath;
    }
}