// package us.dot.its.jpo.conflictmonitor.monitor;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.math.BigDecimal;

// import org.geotools.geometry.jts.JTSFactoryFinder;
// import org.geotools.util.URLs;
// import org.json.JSONArray;
// import org.json.JSONObject;
// import org.locationtech.jts.geom.Coordinate;
// import org.locationtech.jts.geom.CoordinateSequence;
// import org.locationtech.jts.geom.GeometryFactory;
// import org.locationtech.jts.geom.LineString;
// import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
// import us.dot.its.jpo.ode.model.OdeMapMetadata;
// import us.dot.its.jpo.ode.model.OdeMapPayload;
// import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometry;
// import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
// import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
// import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointXY;
// import us.dot.its.jpo.ode.plugin.j2735.J2735NodeLLmD64b;
// import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;
// import us.dot.its.jpo.ode.util.JsonUtils;
// import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
// import us.dot.its.jpo.ode.wrapper.MessageProducer;

// public class MapGeoJsonHandler extends AbstractSubscriberProcessor<String, String> {
//   private Logger logger = LoggerFactory.getLogger(this.getClass());

// 	private ConflictMonitorProperties geojsonProperties;
// 	private MessageProducer<String, String> geoJsonProducer;

// 	public MapGeoJsonHandler(ConflictMonitorProperties geojsonProps) {
// 		super();
// 		this.geojsonProperties = geojsonProps;
// 		this.geoJsonProducer = MessageProducer.defaultStringMessageProducer(geojsonProperties.getKafkaBrokers(),
// 			geojsonProperties.getKafkaProducerType(), geojsonProperties.getKafkaTopicsDisabledSet());
// 	}

// 	@Override
// 	public Object process(String consumedData) {
// 		try {

// 			System.out.println("Received New GeoJSON Map Message");
// 			ObjectMapper mapper = new ObjectMapper();

// 			JsonNode actualObj = mapper.readTree(consumedData);


// 			JsonNode featuresNode = actualObj.get("features");

// 			//System.out.println(featuresNode);
// 			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

// 			LineString[] lanes = new LineString[featuresNode.size()];
// 			for(int i =0; i < featuresNode.size(); i++){
				

// 				//System.out.println(featuresNode.get(i));
				
// 				JsonNode coordinatesNode = featuresNode.get(i).get("geometry").get("coordinates");
// 				Coordinate[] coordList = new Coordinate [coordinatesNode.size()]; 
// 				for(int j=0; j< coordinatesNode.size(); j++){
// 					//Coordinate coord = new Coordinate(coordinatesNode.get(j))
// 					double longitude = coordinatesNode.get(j).get(0).asDouble();
// 					double latitude = coordinatesNode.get(j).get(1).asDouble();
// 					coordList[j] = new Coordinate(longitude, latitude);
// 				}

// 				PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(coordList);
// 				//PackedCoordinateSequence sequence = coordList;
// 				LineString lane = new LineString(sequence, geometryFactory);
// 				lanes[i] = lane;
// 			}

			
// 			for(int i =0; i< lanes.length; i++){
// 				LineString laneA = lanes[i];
// 				for(int j =0; j< lanes.length;j++){
// 					LineString laneB = lanes[j];
// 					if(laneA != laneB){
// 						if(laneA.intersects(laneB)){
// 							System.out.println("Lane" + i + "intersects Lane" + j);
// 						}
// 					}
// 				}
// 			}
// 		} catch (Exception e) {
// 			logger.error("Failed to convert received data to GeoJSON: " + consumedData, e);
// 		}
// 		return null;
//   }

// 	public JSONObject createGeoJsonFromIntersection(J2735IntersectionGeometry intersection, String rsuIp) {
// 		JSONObject intersectionGeoJson = new JSONObject();
// 		intersectionGeoJson.put("type", "FeatureCollection");

// 		OdePosition3D refPoint = intersection.getRefPoint();

// 		JSONArray laneList = new JSONArray();
// 		for (int i = 0; i < intersection.getLaneSet().getLaneSet().size(); i++) {
// 			J2735GenericLane lane = intersection.getLaneSet().getLaneSet().get(i);

// 			// Create the lane feature as a single JSON object
// 			JSONObject laneGeoJson = new JSONObject();
// 			laneGeoJson.put("type", "Feature");

// 			// Create the lane feature properties ---------------------
// 			JSONObject propertiesGeoJson = new JSONObject();
// 			propertiesGeoJson.put("ip", rsuIp);
// 			propertiesGeoJson.put("laneID", lane.getLaneID());

// 			// Add ingress/egress data
// 			propertiesGeoJson.put("ingressPath", lane.getLaneAttributes().getDirectionalUse().get("ingressPath"));
// 			propertiesGeoJson.put("egressPath", lane.getLaneAttributes().getDirectionalUse().get("egressPath"));

// 			if (lane.getIngressApproach() != null)
// 				propertiesGeoJson.put("ingressApproach", lane.getIngressApproach());
// 			else
// 				propertiesGeoJson.put("ingressApproach", 0);
			
// 			if (lane.getEgressApproach() != null)
// 				propertiesGeoJson.put("egressApproach", lane.getEgressApproach());
// 			else
// 				propertiesGeoJson.put("egressApproach", 0);

// 			// Add connected lanes
// 			JSONArray connectsToList = new JSONArray();
// 			if (lane.getConnectsTo() != null) {
// 				for (int x = 0; x < lane.getConnectsTo().getConnectsTo().size(); x++) {
// 					Integer connectsToLane = lane.getConnectsTo().getConnectsTo().get(x).getConnectingLane().getLane();
// 					connectsToList.put(connectsToLane);
// 				}
// 			}
// 			propertiesGeoJson.put("connectedLanes", connectsToList);
// 			laneGeoJson.put("properties", propertiesGeoJson);

// 			// Create the lane feature geometry ---------------------
// 			JSONObject geometryGeoJson = new JSONObject();
// 			geometryGeoJson.put("type", "LineString");

// 			// Calculate coordinates
// 			BigDecimal anchorLat = new BigDecimal(refPoint.getLatitude().toString());
// 			BigDecimal anchorLong = new BigDecimal(refPoint.getLongitude().toString());
// 			JSONArray coordinatesList = new JSONArray();
// 			for (int x = 0; x < lane.getNodeList().getNodes().getNodes().size(); x++) {
// 				J2735NodeOffsetPointXY nodeOffset = lane.getNodeList().getNodes().getNodes().get(x).getDelta();

// 				if (nodeOffset.getNodeLatLon() != null) {
// 					J2735NodeLLmD64b nodeLatLong = nodeOffset.getNodeLatLon();
// 					// Complete absolute lat-long representation per J2735 
// 					// Lat-Long values expressed in standard SAE 1/10 of a microdegree
// 					BigDecimal lat = nodeLatLong.getLat().divide(new BigDecimal("10000000"));
// 					BigDecimal lon = nodeLatLong.getLon().divide(new BigDecimal("10000000"));

// 					JSONArray coordinate = new JSONArray();
// 					coordinate.put(lon.doubleValue());
// 					coordinate.put(lat.doubleValue());
// 					coordinatesList.put(coordinate);

// 					// Reset the anchor point for following offset nodes
// 					// J2735 is not clear if only one of these nodelatlon types is allowed in the lane path nodes
// 					anchorLat = new BigDecimal(lat.toString());
// 					anchorLong = new BigDecimal(lon.toString());
// 				}
// 				else {
// 					// Get the NodeXY object or skip node if entirely null
// 					J2735Node_XY nodexy = null;
// 					if (nodeOffset.getNodeXY1() != null)
// 						nodexy = nodeOffset.getNodeXY1();
// 					else if (nodeOffset.getNodeXY2() != null)
// 						nodexy = nodeOffset.getNodeXY2();
// 					else if (nodeOffset.getNodeXY3() != null)
// 						nodexy = nodeOffset.getNodeXY3();
// 					else if (nodeOffset.getNodeXY4() != null)
// 						nodexy = nodeOffset.getNodeXY4();
// 					else if (nodeOffset.getNodeXY5() != null)
// 						nodexy = nodeOffset.getNodeXY5();
// 					else if (nodeOffset.getNodeXY6() != null)
// 						nodexy = nodeOffset.getNodeXY6();
// 					else
// 						continue;
					
// 					// Calculate offset lon,lat values
// 					// Equations may become less accurate the futher N/S the coordinate is
// 					double offsetX = nodexy.getX().doubleValue();
// 					double offsetY = nodexy.getY().doubleValue();

// 					// (offsetX * 0.01) / (math.cos((Math.PI / 180.0) * anchorLat) * 111111.0)
// 					// Step 1. (offsetX * 0.01)
// 					// Step 2. (math.cos((Math.PI/180.0) * anchorLat) * 111111.0)
// 					// Step 3. Step 1 / Step 2
// 					double offsetX_step1 = offsetX * 0.01;
// 					double offsetX_step2 = Math.cos(((double)(Math.PI / 180.0)) * anchorLat.doubleValue()) * 111111.0;
// 					double offsetXDegrees = offsetX_step1 / offsetX_step2;
					
// 					// (offsetY * 0.01) / 111111.0
// 					double offsetYDegrees = (offsetY * 0.01) / 111111.0;

// 					// return (reference_point[0] + dx_deg, reference_point[1] + dy_deg)
// 					BigDecimal offsetLong = new BigDecimal(String.valueOf(anchorLong.doubleValue() + offsetXDegrees));
// 					BigDecimal offsetLat = new BigDecimal(String.valueOf(anchorLat.doubleValue() + offsetYDegrees));

// 					JSONArray coordinate = new JSONArray();
// 					coordinate.put(offsetLong.doubleValue());
// 					coordinate.put(offsetLat.doubleValue());
// 					coordinatesList.put(coordinate);

// 					// Reset the anchor point for following offset nodes
// 					anchorLat = new BigDecimal(offsetLat.toString());
// 					anchorLong = new BigDecimal(offsetLong.toString());
// 				}
// 			}

// 			geometryGeoJson.put("coordinates", coordinatesList);
// 			laneGeoJson.put("geometry", geometryGeoJson);

// 			// Finally, add lane feature to feature list ---------------------
// 			laneList.put(laneGeoJson);
// 		}

// 		intersectionGeoJson.put("features", laneList);

// 		return intersectionGeoJson;
// 	}
// }