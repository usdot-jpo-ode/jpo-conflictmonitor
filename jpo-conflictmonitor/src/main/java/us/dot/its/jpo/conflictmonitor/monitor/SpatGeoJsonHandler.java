// package us.dot.its.jpo.conflictmonitor.monitor;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


// import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
// import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
// import us.dot.its.jpo.ode.wrapper.MessageProducer;

// public class SpatGeoJsonHandler extends AbstractSubscriberProcessor<String, String> {
//   private Logger logger = LoggerFactory.getLogger(this.getClass());

// 	private ConflictMonitorProperties geojsonProperties;
// 	private MessageProducer<String, String> geoJsonProducer;

// 	public SpatGeoJsonHandler(ConflictMonitorProperties geojsonProps) {
// 		super();
// 		this.geojsonProperties = geojsonProps;
// 		this.geoJsonProducer = MessageProducer.defaultStringMessageProducer(geojsonProperties.getKafkaBrokers(),
// 			geojsonProperties.getKafkaProducerType(), geojsonProperties.getKafkaTopicsDisabledSet());
// 	}

// 	@Override
// 	public Object process(String consumedData) {
// 		try {

// 			System.out.println("Spat Message: " + consumedData);

// 		} catch (Exception e) {
// 			logger.error("Failed to convert received data to GeoJSON: " + consumedData, e);
// 		}
// 		return null;
//   }
// }