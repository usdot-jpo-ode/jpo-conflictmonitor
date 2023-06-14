package us.dot.its.jpo.conflictmonitor.monitor;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.models.MapIntersection;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometry;
import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class MapHandler extends AbstractSubscriberProcessor<String, String> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConflictMonitorProperties geojsonProperties;
	private MessageProducer<String, String> geoJsonProducer;
	protected final ObjectMapper mapper = new ObjectMapper();

	public MapHandler(ConflictMonitorProperties geojsonProps) {
		super();
		this.geojsonProperties = geojsonProps;
		this.geoJsonProducer = MessageProducer.defaultStringMessageProducer(geojsonProperties.getKafkaBrokers(),
				geojsonProperties.getKafkaProducerType(), geojsonProperties.getKafkaTopicsDisabledSet());
	}

	@Override
	public Object process(String consumedData) {
		try {
			OdeMapData map = deserializeMap(consumedData);
			extractIntersections(map);

		} catch (Exception e) {
			logger.error("Failed to convert received data to GeoJSON: ", e);
		}
		return null;
	}

	public void extractIntersections(OdeMapData map) {
		OdeMapPayload payload = (OdeMapPayload) map.getPayload();
		J2735MAP payloadMap = payload.getMap();
		List<J2735IntersectionGeometry> intersections = payloadMap.getIntersections().getIntersections();

		for (J2735IntersectionGeometry intersection : intersections) {
			MapIntersection conflictIntersection = new MapIntersection(intersection);
		}
	}

	public OdeMapData deserializeMap(String mapString) {
		if (mapString == null) {
			return null;
		}
		// try {
		JsonNode actualObj;
		try {
			actualObj = mapper.readTree(mapString);
			JsonNode metadataNode = actualObj.get("metadata");
			String metadataString = metadataNode.toString();
			OdeMapMetadata metadataObject = (OdeMapMetadata) JsonUtils.fromJson(metadataString, OdeMapMetadata.class);
			// Deserialize the payload
			JsonNode payloadNode = actualObj.get("payload");
			String payloadString = payloadNode.toString();
			OdeMapPayload mapPayload = (OdeMapPayload) JsonUtils.fromJson(payloadString, OdeMapPayload.class);
			OdeMapData returnData = new OdeMapData(metadataObject, mapPayload);
			return returnData;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}