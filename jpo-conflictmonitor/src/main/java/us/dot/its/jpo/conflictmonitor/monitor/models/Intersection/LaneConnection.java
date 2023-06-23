package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

public class LaneConnection {
    
    private Lane ingressLane;
    private Lane egressLane;
    private int connectionId;
    private int signalGroup;
    private LineString connectingLineString;

    


    public LaneConnection(Lane ingressLane, Lane egressLane, int connectionId, int signalGroup){
        this.ingressLane = ingressLane;
        this.egressLane = egressLane;
        this.connectionId = connectionId;
        this.signalGroup = signalGroup;
        this.connectingLineString = calculateConnectingLineString(25);
    }


    // computes the points comprising a linestring connecting the ingress and egress lanes.
    public LineString calculateConnectingLineString(int numIntermediatePoints){

        int egressPointCount = egressLane.getPoints().getNumPoints();
        Point startPoint = ingressLane.getPoints().getPointN(0);
        Point leadInPoint = ingressLane.getPoints().getPointN(1);

        Point endPoint = egressLane.getPoints().getPointN(egressPointCount-1);
        Point leadOutPoint = egressLane.getPoints().getPointN(egressPointCount-2);

        
        
        // // Coordinate[] startCoordinates = ingressPath.getCoordinates();
        // // Coordinate[] endCoordinates = egressPath.getCoordinates();

        // // Coordinate leadInPoint = startCoordinates[startCoordinates.length - 2];
        // // Coordinate startPoint = startCoordinates[startCoordinates.length - 1];

        // // Coordinate endPoint = endCoordinates[0];
        // // Coordinate leadOutPoint = endCoordinates[1];

        // // Clamp the slope of the spline based upon how far it needs to go. This bounds
        // // each spline and prevents splines from folding back on themselves.
        // // Currently the L1 norm is being used, however both L1 and L2 produce
        // // acceptable results.
        // // Increasing the clamp value increases how much each interpolation can curve.
        // // However if the threshold is set too high, the curves will fold back on
        // // themselves.

        // // double segmentLength = Math.sqrt(Math.pow(startPoint.x - endPoint.x,2) +
        // // Math.pow(startPoint.y - endPoint.y,2)); //L2 Norm
        // double segmentLength = Math.abs((startPoint.getX() - endPoint.getX())) + Math.abs((startPoint.getY() - endPoint.getY())); // L1 Norm

        // double xInSlope = MathFunctions.clamp((startPoint.getX() - leadInPoint.getX()), segmentLength);
        // double yInSlope = MathFunctions.clamp((startPoint.getY() - leadInPoint.getY()), segmentLength);
        // double xOutSlope = MathFunctions.clamp((leadOutPoint.getX() - endPoint.getX()), segmentLength);
        // double yOutSlope = MathFunctions.clamp((leadOutPoint.getY() - endPoint.getY()), segmentLength);

        // double[][] equations = {
        //         { 1, 1, 1, 1 }, // equals startPoint (x,y)
        //         { 1, 2, 4, 8 }, // equals endPoint (x,y)
        //         { 0, 1, 2, 3 }, // equals inSlope (x,y)
        //         { 0, 1, 4, 12 }, // equals outSlope (x,y)
        // };

        // double[][] solutions = {
        //         { startPoint.getX(), startPoint.getY()},
        //         { endPoint.getX(), endPoint.getY()},
        //         { xInSlope, yInSlope },
        //         { xOutSlope, yOutSlope }
        // };

        // RealMatrix equationsMatrix = new Array2DRowRealMatrix(equations);
        // RealMatrix solutionMatrix = new Array2DRowRealMatrix(solutions);
        // DecompositionSolver solver = new LUDecomposition(equationsMatrix).getSolver();
        // RealMatrix solution = solver.solve(solutionMatrix);

        

        // for (int i = 0; i < interpolationPoints; i++) {
        //     connection[i + 1] = evaluatePoint(solution, 1 + (i / (float) (interpolationPoints + 1.0)));
        // }

        Coordinate[] connection = new Coordinate[2];
        connection[0] = new Coordinate(startPoint.getX(), startPoint.getY());
        connection[1] = new Coordinate(endPoint.getX(), endPoint.getY());

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(connection);
        return new LineString(sequence, this.ingressLane.getGeometryFactory());
    }

    public boolean crosses(LaneConnection otherConnection){
        return this.connectingLineString.crosses(otherConnection.getConnectingLineString());
    }

    public LineString getConnectingLineString() {
        return connectingLineString;
    }

    public void setConnectingLineString(LineString connectingLineString) {
        this.connectingLineString = connectingLineString;
    }

    public Lane getIngressLane() {
        return ingressLane;
    }

    public void setIngressLane(Lane ingressLane) {
        this.ingressLane = ingressLane;
    }
    
    public Lane getEgressLane() {
        return egressLane;
    }

    public void setEgressLane(Lane egressLane) {
        this.egressLane = egressLane;
    }
    
    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public int getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

}
