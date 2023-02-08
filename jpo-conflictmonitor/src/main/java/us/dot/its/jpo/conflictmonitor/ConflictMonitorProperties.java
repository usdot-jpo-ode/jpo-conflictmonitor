/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.conflictmonitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SystemUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.thymeleaf.util.StringUtils;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.map.MapTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.map.MapTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithmFactory;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.plugin.OdePlugin;
import us.dot.its.jpo.ode.util.CommonUtils;

@ConfigurationProperties
public class ConflictMonitorProperties implements EnvironmentAware {

   private static final Logger logger = LoggerFactory.getLogger(ConflictMonitorProperties.class);

   @Autowired
   private Environment env;

   private MapValidationAlgorithmFactory mapValidationAlgorithmFactory;
   private SpatValidationStreamsAlgorithmFactory spatValidationAlgorithmFactory;
   private String mapValidationAlgorithm;
   private String spatValidationAlgorithm;
   private SpatValidationParameters spatValidationParameters;
   private MapValidationParameters mapValidationParameters;

  
   private LaneDirectionOfTravelAlgorithmFactory laneDirectionOfTravelAlgorithmFactory;
   private String laneDirectionOfTravelAlgorithm;
   private LaneDirectionOfTravelParameters laneDirectionOfTravelParameters;
   
   private ConnectionOfTravelAlgorithmFactory connectionOfTravelAlgorithmFactory;
   private String connectionOfTravelAlgorithm;
   private ConnectionOfTravelParameters connectionOfTravelParameters;

   private SignalStateVehicleCrossesAlgorithmFactory signalStateVehicleCrossesAlgorithmFactory;
   private String signalStateVehicleCrossesAlgorithm;
   private SignalStateVehicleCrossesParameters signalStateVehicleCrossesParameters;

   private SignalStateVehicleStopsAlgorithmFactory signalStateVehicleStopsAlgorithmFactory;
   private String signalStateVehicleStopsAlgorithm;
   private SignalStateVehicleStopsParameters signalStateVehicleStopsParameters;

   private MapSpatMessageAssessmentAlgorithmFactory mapSpatMessageAssessmentAlgorithmFactory;
   private String mapSpatMessageAssessmentAlgorithm;
   private MapSpatMessageAssessmentParameters mapSpatMessageAssessmentParameters;

   private SpatTimeChangeDetailsAlgorithmFactory spatTimeChangeDetailsAlgorithmFactory;
   private String spatTimeChangeDetailsAlgorithm;
   private SpatTimeChangeDetailsParameters spatTimeChangeDetailsParameters;

   private MapTimeChangeDetailsAlgorithmFactory mapTimeChangeDetailsAlgorithmFactory;
   private String mapTimeChangeDetailsAlgorithm;
   private MapTimeChangeDetailsParameters mapTimeChangeDetailsParameters;

   private SignalStateEventAssessmentAlgorithmFactory signalStateEventAssessmentAlgorithmFactory;
   private String signalStateEventAssessmentAlgorithm;
   private SignalStateEventAssessmentParameters signalStateEventAssessmentAlgorithmParameters;

   private LaneDirectionOfTravelAssessmentAlgorithmFactory laneDirectionOfTravelAssessmentAlgorithmFactory;
   private String laneDirectionOfTravelAssessmentAlgorithm;
   private LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmentAlgorithmParameters;

   private ConnectionOfTravelAssessmentAlgorithmFactory connectionOfTravelAssessmentAlgorithmFactory;
   private String connectionOfTravelAssessmentAlgorithm;
   private ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmentAlgorithmParameters;

   private RepartitionAlgorithmFactory repartitionAlgorithmFactory;
   private String repartitionAlgorithm;
   private RepartitionParameters repartitionAlgorithmParameters;

   private IntersectionEventAlgorithmFactory intersectionEventAlgorithmFactory;
   private String intersectionEventAlgorithm;

   public IntersectionEventAlgorithmFactory getIntersectionEventAlgorithmFactory() {
      return this.intersectionEventAlgorithmFactory;
   }

   @Autowired
   public void setIntersectionEventAlgorithmFactory(IntersectionEventAlgorithmFactory intersectionEventAlgorithmFactory) {
      this.intersectionEventAlgorithmFactory = intersectionEventAlgorithmFactory;
   }

   public String getIntersectionEventAlgorithm() {
      return this.intersectionEventAlgorithm;
   }

   @Value("${intersection.event.algorithm}")
   public void setIntersectionEventAlgorithm(String intersectionEventAlgorithm) {
      this.intersectionEventAlgorithm = intersectionEventAlgorithm;
   }

   @Autowired
   public void setMapValidationParameters(MapValidationParameters mapBroadcastRateParameters) {
      this.mapValidationParameters = mapBroadcastRateParameters;
   }

   @Autowired
   public void setSpatValidationParameters(SpatValidationParameters spatBroadcastRateParameters) {
      this.spatValidationParameters = spatBroadcastRateParameters;
   }

   @Autowired
   public void setMapValidationAlgorithmFactory(MapValidationAlgorithmFactory factory) {
      this.mapValidationAlgorithmFactory = factory;
   }

   @Autowired
   public void setSpatValidationAlgorithmFactory(SpatValidationStreamsAlgorithmFactory factory) {
      this.spatValidationAlgorithmFactory = factory;
   }

   @Value("${map.validation.algorithm}")
   public void setMapValidationAlgorithm(String mapBroadcastRateAlgorithm) {
      this.mapValidationAlgorithm = mapBroadcastRateAlgorithm;
   }

   @Value("${spat.validation.algorithm}")
   public void setSpatValidationAlgorithm(String spatBroadcastRateAlgorithm) {
      this.spatValidationAlgorithm = spatBroadcastRateAlgorithm;
   }

   public MapValidationAlgorithmFactory getMapValidationAlgorithmFactory() {
      return mapValidationAlgorithmFactory;
   }

   public SpatValidationStreamsAlgorithmFactory getSpatValidationAlgorithmFactory() {
      return spatValidationAlgorithmFactory;
   }

   public String getMapValidationAlgorithm() {
      return this.mapValidationAlgorithm;
   }

   public String getSpatValidationAlgorithm() {
      return this.spatValidationAlgorithm;
   }

   public SpatValidationParameters getSpatValidationParameters() {
      return this.spatValidationParameters;
   }

   public MapValidationParameters getMapValidationParameters() {
      return this.mapValidationParameters;
   }

   public LaneDirectionOfTravelAlgorithmFactory getLaneDirectionOfTravelAlgorithmFactory() {
      return laneDirectionOfTravelAlgorithmFactory;
   }

   @Autowired
   public void setLaneDirectionOfTravelAlgorithmFactory(
         LaneDirectionOfTravelAlgorithmFactory laneDirectionOfTravelAlgorithmFactory) {
      this.laneDirectionOfTravelAlgorithmFactory = laneDirectionOfTravelAlgorithmFactory;
   }

   public String getLaneDirectionOfTravelAlgorithm() {
      return laneDirectionOfTravelAlgorithm;
   }

   @Value("${lane.direction.of.travel.algorithm}")
   public void setLaneDirectionOfTravelAlgorithm(String laneDirectionOfTravelAlgorithm) {
      this.laneDirectionOfTravelAlgorithm = laneDirectionOfTravelAlgorithm;
   }

   public LaneDirectionOfTravelParameters getLaneDirectionOfTravelParameters() {
      return laneDirectionOfTravelParameters;
   }

   @Autowired
   public void setLaneDirectionOfTravelParameters(LaneDirectionOfTravelParameters laneDirectionOfTravelParameters) {
      this.laneDirectionOfTravelParameters = laneDirectionOfTravelParameters;
   }

   public ConnectionOfTravelAlgorithmFactory getConnectionOfTravelAlgorithmFactory() {
      return connectionOfTravelAlgorithmFactory;
   }

   @Autowired
   public void setConnectionOfTravelAlgorithmFactory(
         ConnectionOfTravelAlgorithmFactory connectionOfTravelAlgorithmFactory) {
      this.connectionOfTravelAlgorithmFactory = connectionOfTravelAlgorithmFactory;
   }

   public String getConnectionOfTravelAlgorithm() {
      return connectionOfTravelAlgorithm;
   }

   @Value("${connection.of.travel.algorithm}")
   public void setConnectionOfTravelAlgorithm(String connectionOfTravelAlgorithm) {
      this.connectionOfTravelAlgorithm = connectionOfTravelAlgorithm;
   }

   public ConnectionOfTravelParameters getConnectionOfTravelParameters() {
      return connectionOfTravelParameters;
   }
   
   @Autowired
   public void setConnectionOfTravelParameters(ConnectionOfTravelParameters connectionOfTravelParameters) {
      this.connectionOfTravelParameters = connectionOfTravelParameters;
   }

   public SignalStateVehicleCrossesAlgorithmFactory getSignalStateVehicleCrossesAlgorithmFactory() {
      return signalStateVehicleCrossesAlgorithmFactory;
   }

   @Autowired
   public void setSignalStateVehicleCrossesAlgorithmFactory(
         SignalStateVehicleCrossesAlgorithmFactory signalStateVehicleCrossesAlgorithmFactory) {
      this.signalStateVehicleCrossesAlgorithmFactory = signalStateVehicleCrossesAlgorithmFactory;
   }
   
   public String getSignalStateVehicleCrossesAlgorithm() {
      return signalStateVehicleCrossesAlgorithm;
   }

   @Value("${signal.state.vehicle.crosses.algorithm}")
   public void setSignalStateVehicleCrossesAlgorithm(String signalStateVehicleCrossesAlgorithm) {
      this.signalStateVehicleCrossesAlgorithm = signalStateVehicleCrossesAlgorithm;
   }

   public SignalStateVehicleCrossesParameters getSignalStateVehicleCrossesParameters() {
      return signalStateVehicleCrossesParameters;
   }

   @Autowired
   public void setSignalStateVehicleCrossesParameters(
         SignalStateVehicleCrossesParameters signalStateVehicleCrossesParameters) {
      this.signalStateVehicleCrossesParameters = signalStateVehicleCrossesParameters;
   }

   public SignalStateVehicleStopsAlgorithmFactory getSignalStateVehicleStopsAlgorithmFactory() {
      return signalStateVehicleStopsAlgorithmFactory;
   }

   @Autowired
   public void setSignalStateVehicleStopsAlgorithmFactory(
         SignalStateVehicleStopsAlgorithmFactory signalStateVehicleStopsAlgorithmFactory) {
      this.signalStateVehicleStopsAlgorithmFactory = signalStateVehicleStopsAlgorithmFactory;
   }

   public String getSignalStateVehicleStopsAlgorithm() {
      return signalStateVehicleStopsAlgorithm;
   }

   @Value("${signal.state.vehicle.stops.algorithm}")
   public void setSignalStateVehicleStopsAlgorithm(String signalStateVehicleStopsAlgorithm) {
      this.signalStateVehicleStopsAlgorithm = signalStateVehicleStopsAlgorithm;
   }

   public SignalStateVehicleStopsParameters getSignalStateVehicleStopsParameters() {
      return signalStateVehicleStopsParameters;
   }

   @Autowired
   public void setSignalStateVehicleStopsParameters(SignalStateVehicleStopsParameters signalStateVehicleStopsParameters) {
      this.signalStateVehicleStopsParameters = signalStateVehicleStopsParameters;
   }

   public MapSpatMessageAssessmentAlgorithmFactory getMapSpatMessageAssessmentAlgorithmFactory() {
      return mapSpatMessageAssessmentAlgorithmFactory;
   }

   @Autowired
   public void setMapSpatMessageAssessmentAlgorithmFactory(
         MapSpatMessageAssessmentAlgorithmFactory mapSpatMessageAssessmentAlgorithmFactory) {
      this.mapSpatMessageAssessmentAlgorithmFactory = mapSpatMessageAssessmentAlgorithmFactory;
   }
   
   @Value("${map.spat.message.assessment.algorithm}")
   public String getMapSpatMessageAssessmentAlgorithm() {
      return mapSpatMessageAssessmentAlgorithm;
   }

   public void setMapSpatMessageAssessmentAlgorithm(String mapSpatMessageAssessmentAlgorithm) {
      this.mapSpatMessageAssessmentAlgorithm = mapSpatMessageAssessmentAlgorithm;
   }

   public MapSpatMessageAssessmentParameters getMapSpatMessageAssessmentParameters() {
      return mapSpatMessageAssessmentParameters;
   }

   @Autowired
   public void setMapSpatMessageAssessmentParameters(
         MapSpatMessageAssessmentParameters mapSpatMessageAssessmentParameters) {
      this.mapSpatMessageAssessmentParameters = mapSpatMessageAssessmentParameters;
   }

   public SpatTimeChangeDetailsAlgorithmFactory getSpatTimeChangeDetailsAlgorithmFactory() {
      return spatTimeChangeDetailsAlgorithmFactory;
   }


   @Autowired
   public void setSpatTimeChangeDetailsAlgorithmFactory(
         SpatTimeChangeDetailsAlgorithmFactory spatTimeChangeDetailsAlgorithmFactory) {
      this.spatTimeChangeDetailsAlgorithmFactory = spatTimeChangeDetailsAlgorithmFactory;
   }

   public String getSpatTimeChangeDetailsAlgorithm() {
      return spatTimeChangeDetailsAlgorithm;
   }

   @Value("${spat.time.change.details.algorithm}")
   public void setSpatTimeChangeDetailsAlgorithm(String spatTimeChangeDetailsAlgorithm) {
      this.spatTimeChangeDetailsAlgorithm = spatTimeChangeDetailsAlgorithm;
   }

   public SpatTimeChangeDetailsParameters getSpatTimeChangeDetailsParameters() {
      return spatTimeChangeDetailsParameters;
   }

   @Autowired
   public void setSpatTimeChangeDetailsParameters(SpatTimeChangeDetailsParameters spatTimeChangeDetailsParameters) {
      this.spatTimeChangeDetailsParameters = spatTimeChangeDetailsParameters;
   }

   public MapTimeChangeDetailsAlgorithmFactory getMapTimeChangeDetailsAlgorithmFactory() {
      return mapTimeChangeDetailsAlgorithmFactory;
   }

   @Autowired
   public void setMapTimeChangeDetailsAlgorithmFactory(
         MapTimeChangeDetailsAlgorithmFactory mapTimeChangeDetailsAlgorithmFactory) {
      this.mapTimeChangeDetailsAlgorithmFactory = mapTimeChangeDetailsAlgorithmFactory;
   }

   public String getMapTimeChangeDetailsAlgorithm() {
      return mapTimeChangeDetailsAlgorithm;
   }

   @Value("${map.time.change.details.algorithm}")
   public void setMapTimeChangeDetailsAlgorithm(String mapTimeChangeDetailsAlgorithm) {
      this.mapTimeChangeDetailsAlgorithm = mapTimeChangeDetailsAlgorithm;
   }

   public MapTimeChangeDetailsParameters getMapTimeChangeDetailsParameters() {
      return mapTimeChangeDetailsParameters;
   }

   @Autowired
   public void setMapTimeChangeDetailsParameters(MapTimeChangeDetailsParameters mapTimeChangeDetailsParameters) {
      this.mapTimeChangeDetailsParameters = mapTimeChangeDetailsParameters;
   }


   public SignalStateEventAssessmentAlgorithmFactory getSignalStateEventAssessmentAlgorithmFactory() {
      return signalStateEventAssessmentAlgorithmFactory;
   }

   @Autowired
   public void setSignalStateEventAssessmentAlgorithmFactory(
         SignalStateEventAssessmentAlgorithmFactory signalStateEventAssessmentAlgorithmFactory) {
      this.signalStateEventAssessmentAlgorithmFactory = signalStateEventAssessmentAlgorithmFactory;
   }

   
   public String getSignalStateEventAssessmentAlgorithm() {
      return signalStateEventAssessmentAlgorithm;
   }

   @Value("${signal.state.event.assessment.algorithm}")
   public void setSignalStateEventAssessmentAlgorithm(String signalStateEventAssessmentAlgorithm) {
      this.signalStateEventAssessmentAlgorithm = signalStateEventAssessmentAlgorithm;
   }

   public SignalStateEventAssessmentParameters getSignalStateEventAssessmentAlgorithmParameters() {
      return signalStateEventAssessmentAlgorithmParameters;
   }

   @Autowired
   public void setSignalStateEventAssessmentAlgorithmParameters(
      SignalStateEventAssessmentParameters signalStateEventAssessmentAlgorithmParameters) {
      this.signalStateEventAssessmentAlgorithmParameters = signalStateEventAssessmentAlgorithmParameters;
   }

   public LaneDirectionOfTravelAssessmentAlgorithmFactory getLaneDirectionOfTravelAssessmentAlgorithmFactory() {
      return laneDirectionOfTravelAssessmentAlgorithmFactory;
   }

   @Autowired
   public void setLaneDirectionOfTravelAssessmentAlgorithmFactory(
         LaneDirectionOfTravelAssessmentAlgorithmFactory laneDirectionfOfTravelAssessmentAlgorithmFactory) {
      this.laneDirectionOfTravelAssessmentAlgorithmFactory = laneDirectionfOfTravelAssessmentAlgorithmFactory;
   }

   public String getLaneDirectionOfTravelAssessmentAlgorithm() {
      return laneDirectionOfTravelAssessmentAlgorithm;
   }

   @Value("${lane.direction.of.travel.assessment.algorithm}")
   public void setLaneDirectionOfTravelAssessmentAlgorithm(String laneDirectionOfTravelAssessmentAlgorithm) {
      this.laneDirectionOfTravelAssessmentAlgorithm = laneDirectionOfTravelAssessmentAlgorithm;
   }

   public LaneDirectionOfTravelAssessmentParameters getLaneDirectionOfTravelAssessmentAlgorithmParameters() {
      return laneDirectionOfTravelAssessmentAlgorithmParameters;
   }

   @Autowired
   public void setLaneDirectionOfTravelAssessmentAlgorithmParameters(
         LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmentAlgorithmParameters) {
      this.laneDirectionOfTravelAssessmentAlgorithmParameters = laneDirectionOfTravelAssessmentAlgorithmParameters;
   }

   public ConnectionOfTravelAssessmentAlgorithmFactory getConnectionOfTravelAssessmentAlgorithmFactory() {
      return connectionOfTravelAssessmentAlgorithmFactory;
   }

   @Autowired
   public void setConnectionOfTravelAssessmentAlgorithmFactory(
         ConnectionOfTravelAssessmentAlgorithmFactory connectionOfTravelAssessmentAlgorithmFactory) {
      this.connectionOfTravelAssessmentAlgorithmFactory = connectionOfTravelAssessmentAlgorithmFactory;
   }
   
   public String getConnectionOfTravelAssessmentAlgorithm() {
      return connectionOfTravelAssessmentAlgorithm;
   }

   @Value("${connection.of.travel.assessment.algorithm}")
   public void setConnectionOfTravelAssessmentAlgorithm(String connectionOfTravelAssessmentAlgorithm) {
      this.connectionOfTravelAssessmentAlgorithm = connectionOfTravelAssessmentAlgorithm;
   }

   public ConnectionOfTravelAssessmentParameters getConnectionOfTravelAssessmentAlgorithmParameters() {
      return connectionOfTravelAssessmentAlgorithmParameters;
   }

   @Autowired
   public void setConnectionOfTravelAssessmentAlgorithmParameters(
         ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmentAlgorithmParameters) {
      this.connectionOfTravelAssessmentAlgorithmParameters = connectionOfTravelAssessmentAlgorithmParameters;
   }

   public RepartitionAlgorithmFactory getRepartitionAlgorithmFactory() {
      return repartitionAlgorithmFactory;
   }

   @Autowired
   public void setRepartitionAlgorithmFactory(RepartitionAlgorithmFactory repartitionAlgorithmFactory) {
      this.repartitionAlgorithmFactory = repartitionAlgorithmFactory;
   }

   public String getRepartitionAlgorithm() {
      return repartitionAlgorithm;
   }

   @Value("${repartition.algorithm}")
   public void setRepartitionAlgorithm(String repartitionAlgorithm) {
      this.repartitionAlgorithm = repartitionAlgorithm;
   }

   public RepartitionParameters getRepartitionAlgorithmParameters() {
      return repartitionAlgorithmParameters;
   }

   @Autowired
   public void setRepartitionAlgorithmParameters(RepartitionParameters repartitionAlgorithmParameters) {
      this.repartitionAlgorithmParameters = repartitionAlgorithmParameters;
   }

   public Boolean isVerboseJson() {
      return this.verboseJson;
   }

   public void setHostId(String hostId) {
      this.hostId = hostId;
   }

   public void setUploadLocations(List<Path> uploadLocations) {
      this.uploadLocations = uploadLocations;
   }

   public int getSecuritySvcsPort() {
      return this.securitySvcsPort;
   }

   public void setSecuritySvcsPort(int securitySvcsPort) {
      this.securitySvcsPort = securitySvcsPort;
   }

   public String getSecuritySvcsSignatureEndpoint() {
      return this.securitySvcsSignatureEndpoint;
   }

   public void setSecuritySvcsSignatureEndpoint(String securitySvcsSignatureEndpoint) {
      this.securitySvcsSignatureEndpoint = securitySvcsSignatureEndpoint;
   }

   public String getUploadLocationObuLogLog() {
      return this.uploadLocationObuLogLog;
   }

   public void setUploadLocationObuLogLog(String uploadLocationObuLogLog) {
      this.uploadLocationObuLogLog = uploadLocationObuLogLog;
   }

   public String getDdsCasPass() {
      return this.ddsCasPass;
   }

   public void setDdsCasPass(String ddsCasPass) {
      this.ddsCasPass = ddsCasPass;
   }

   public boolean isDepositSdwMessagesOverWebsocket() {
      return this.depositSdwMessagesOverWebsocket;
   }

   public boolean getDepositSdwMessagesOverWebsocket() {
      return this.depositSdwMessagesOverWebsocket;
   }

   public BuildProperties getBuildProperties() {
      return this.buildProperties;
   }

   public void setBuildProperties(BuildProperties buildProperties) {
      this.buildProperties = buildProperties;
   }

   /*
    * General Properties
    */
   private String version;
   public static final int OUTPUT_SCHEMA_VERSION = 6;
   private String pluginsLocations = "plugins";
   private String kafkaBrokers = null;
   private static final String DEFAULT_KAFKA_PORT = "9092";
   private String kafkaProducerType = AppContext.DEFAULT_KAFKA_PRODUCER_TYPE;
   private Boolean verboseJson = false;
   private int importProcessorBufferSize = OdePlugin.INPUT_STREAM_BUFFER_SIZE;
   private String hostId;
   private List<Path> uploadLocations = new ArrayList<>();

   /*
    * RSU Properties
    */
   private int rsuSrmSlots = 100; // number of "store and repeat message" indicies for RSU TIMs
   private String rsuUsername = "";
   private String rsuPassword = "";

   /*
    * Security Services Module Properties
    */
   private String securitySvcsSignatureUri;
   private int securitySvcsPort = 8090;
   private String securitySvcsSignatureEndpoint = "sign";

   // File import properties
   private String uploadLocationRoot = "uploads";
   private String uploadLocationObuLogLog = "bsmlog";
   private Integer fileWatcherPeriod = 5; // time to wait between processing inbox directory for new files

   /*
    * USDOT Situation Data Clearinghouse (SDC)/ Situation Data Warehouse (SDW),
    * a.k.a Data Distribution System (DDS) Properties
    */
   // DDS WebSocket Properties
   private String ddsCasUrl = "https://cas.cvmvp.com/accounts/v1/tickets";
   private String ddsCasUsername = "";
   private String ddsCasPass = "";
   private String ddsWebsocketUrl = "wss://webapp.cvmvp.com/whtools/websocket";

   // Enable/disable depositing SDW messages over Websocket(true) or REST(false)
   @Value("${ode.depositSdwMessagesOverWebsocket:false}")
   private boolean depositSdwMessagesOverWebsocket = false;

   /*
    * UDP Properties
    */
   private int trustRetries = 2; // if trust handshake fails, how many times to retry
   private int messagesUntilTrustReestablished = 10; // renew trust session every x messages

   /*
    * Kafka Topics
    * 
    */
   private String[] kafkaTopicsDisabled = {
         // disable all POJO topics by default except "topic.OdeBsmPojo". Never
         // "topic.OdeBsmPojo because that's the only way to get data into
         // "topic.OdeBsmJson
         "topic.OdeBsmRxPojo", "topic.OdeBsmTxPojo", "topic.OdeBsmDuringEventPojo", "topic.OdeTimBroadcastPojo" };
   private Set<String> kafkaTopicsDisabledSet = new HashSet<>();

   // BSM
   private String kafkaTopicOdeBsmPojo = "topic.OdeBsmPojo";
   private String kafkaTopicOdeBsmJson = "topic.OdeBsmJson";
   private String kafkaTopicOdeBsmRxPojo = "topic.OdeBsmRxPojo";
   private String kafkaTopicOdeBsmTxPojo = "topic.OdeBsmTxPojo";
   private String kafkaTopicOdeBsmDuringEventPojo = "topic.OdeBsmDuringEventPojo";
   private String kafkaTopicFilteredOdeBsmJson = "topic.FilteredOdeBsmJson";
   private String kafkaTopicOdeRawEncodedBSMJson = "topic.OdeRawEncodedBSMJson";
   private String kafkaTopicCmBsmEvent = "topic.CMBsmEvents";
   private String kafkaTopicBsmRepartition = "topic.BsmJsonRepartition";

   

   private int bsmReceiverPort = 46800;
   private int bsmBufferSize = 500;

   // TIM
   private String kafkaTopicOdeTimJson = "topic.OdeTimJson";
   private String kafkaTopicOdeDNMsgJson = "topic.OdeDNMsgJson";
   private String kafkaTopicOdeTimRxJson = "topic.OdeTimRxJson";
   private String kafkaTopicOdeTimBroadcastPojo = "topic.OdeTimBroadcastPojo";
   private String kafkaTopicOdeTimBroadcastJson = "topic.OdeTimBroadcastJson";
   private String kafkaTopicJ2735TimBroadcastJson = "topic.J2735TimBroadcastJson";
   private String kafkaTopicFilteredOdeTimJson = "topic.FilteredOdeTimJson";
   private String kafkaTopicOdeRawEncodedTIMJson = "topic.OdeRawEncodedTIMJson";
   private int timReceiverPort = 47900;
   private int timBufferSize = 500;

   // SPAT
   private String kafkaTopicOdeSpatTxPojo = "topic.OdeSpatTxPojo";
   private String kafkaTopicOdeSpatPojo = "topic.OdeSpatPojo";
   private String kafkaTopicOdeSpatJson = "topic.OdeSpatJson";
   private String kafkaTopicOdeSpatRxPojo = "topic.OdeSpatRxPojo";
   private String kafkaTopicOdeSpatRxJson = "topic.OdeSpatRxJson";
   private String kafkaTopicFilteredOdeSpatJson = "topic.FilteredOdeSpatJson";
   private String kafkaTopicOdeRawEncodedSPATJson = "topic.OdeRawEncodedSPATJson";
   private String kafkaTopicSpatGeoJson = "spatgeojson-geojson-joined-repartition";// "topic.SpatGeoJson";
   private String kafkaTopicProcessedSpat = "topic.ProcessedSpat";

   private int spatReceiverPort = 44910;
   private int spatBufferSize = 1000;

   // SSM
   private String kafkaTopicOdeSsmPojo = "topic.OdeSsmPojo";
   private String kafkaTopicOdeSsmJson = "topic.OdeSsmJson";
   private String kafkaTopicOdeRawEncodedSSMJson = "topic.OdeRawEncodedSSMJson";
   private int ssmReceiverPort = 44900;
   private int ssmBufferSize = 500;

   // SRM
   private String kafkaTopicOdeSrmTxPojo = "topic.OdeSrmTxPojo";
   private String kafkaTopicOdeSrmJson = "topic.OdeSrmJson";
   private String kafkaTopicOdeRawEncodedSRMJson = "topic.OdeRawEncodedSRMJson";
   private int srmReceiverPort = 44930;
   private int srmBufferSize = 500;

   // MAP
   private String kafkaTopicOdeRawEncodedMAPJson = "topic.OdeRawEncodedMAPJson";
   private String kafkaTopicOdeMapTxPojo = "topic.OdeMapTxPojo";
   private String kafkaTopicOdeMapJson = "topic.OdeMapJson";
   private String kafkaTopicMapGeoJson = "topic.MapGeoJson";
   private String kafkaTopicProcessedMap = "topic.ProcessedMap";
   private int mapReceiverPort = 44920;
   private int mapBufferSize = 2048;

   // DriverAlerts
   private String kafkaTopicDriverAlertJson = "topic.OdeDriverAlertJson";

   // ASN.1 CODEC
   private String kafkaTopicAsn1DecoderInput = "topic.Asn1DecoderInput";
   private String kafkaTopicAsn1DecoderOutput = "topic.Asn1DecoderOutput";
   private String kafkaTopicAsn1EncoderInput = "topic.Asn1EncoderInput";
   private String kafkaTopicAsn1EncoderOutput = "topic.Asn1EncoderOutput";

   //Vehicle Events
   private String kafkaTopicCmVehicleEvent = "topic.CmVehicleEvent";
   private String kafkatopicCmLaneDirectionOfTravelEvent = "topic.CmLaneDirectionOfTravelEvent";
   private String kafkaTopicCmConnectionOfTravelEvent = "topic.CmConnectionOfTravelEvent";
   private String kafkaTopicCmSignalStateEvent = "topic.CmSignalStateEvent";
   private String kafakTopicCmVehicleStopEvent = "topic.CmSignalStopEvent"; 

   // SDW Depositor Module
   private String kafkaTopicSdwDepositorInput = "topic.SDWDepositorInput";

   // Signed Tim with expiration
   private String kafkaTopicSignedOdeTimJsonExpiration = "topic.OdeTIMCertExpirationTimeJson";
   /*
    * Security Properties
    */
   private String caCertPath;
   private String selfCertPath;
   private String selfPrivateKeyReconstructionFilePath;
   private String selfSigningPrivateKeyFilePath;

   private static final byte[] JPO_ODE_GROUP_ID = "jode".getBytes();

   // Conflict Monitor Properties



   @Autowired
   BuildProperties buildProperties;

   @PostConstruct
   void initialize() {
      setVersion(buildProperties.getVersion());
      logger.info("groupId: {}", buildProperties.getGroup());
      logger.info("artifactId: {}", buildProperties.getArtifact());
      logger.info("version: {}", version);
      OdeMsgMetadata.setStaticSchemaVersion(OUTPUT_SCHEMA_VERSION);

      uploadLocations.add(Paths.get(uploadLocationRoot));

      String hostname;
      try {
         hostname = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
         // Let's just use a random hostname
         hostname = UUID.randomUUID().toString();
         logger.info("Unknown host error: {}, using random", e);
      }
      hostId = hostname;
      logger.info("Host ID: {}", hostId);
      EventLogger.logger.info("Initializing services on host {}", hostId);

      if (kafkaBrokers == null) {

         String dockerIp = CommonUtils.getEnvironmentVariable("DOCKER_HOST_IP");

         logger.info("ode.kafkaBrokers property not defined. Will try DOCKER_HOST_IP => {}", kafkaBrokers);

         if (dockerIp == null) {
            logger.warn(
                  "Neither ode.kafkaBrokers ode property nor DOCKER_HOST_IP environment variable are defined. Defaulting to localhost.");
            dockerIp = "localhost";
         }
         kafkaBrokers = dockerIp + ":" + DEFAULT_KAFKA_PORT;

         // URI for the security services /sign endpoint
         if (securitySvcsSignatureUri == null) {
            securitySvcsSignatureUri = "http://" + dockerIp + ":" + securitySvcsPort + "/"
                  + securitySvcsSignatureEndpoint;
         }
      }

      List<String> asList = Arrays.asList(this.getKafkaTopicsDisabled());
      logger.info("Disabled Topics: {}", asList);
      kafkaTopicsDisabledSet.addAll(asList);
   }

   public Properties createStreamProperties(String name) {
      Properties streamProps = new Properties();
      streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, name);

      streamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);

      streamProps.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
            LogAndContinueExceptionHandler.class.getName());

      streamProps.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
            LogAndSkipOnInvalidTimestamp.class.getName());

      streamProps.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG,
            AlwaysContinueProductionExceptionHandler.class.getName());

      streamProps.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);

      streamProps.put(StreamsConfig.producerPrefix("acks"), "all");

      // Reduce cache buffering per topology to 1MB
      streamProps.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 1 * 1024 * 1024L);

      // Decrease default commit interval. Default for 'at least once' mode of 30000ms
      // is too slow.
      streamProps.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);

      // All the keys are Strings in this app
      streamProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

      // Configure the state store location
      if (SystemUtils.IS_OS_LINUX) {
         streamProps.put(StreamsConfig.STATE_DIR_CONFIG, "/var/lib/odd/kafka-streams");
      } else if (SystemUtils.IS_OS_WINDOWS) {
         streamProps.put(StreamsConfig.STATE_DIR_CONFIG, "C:/temp");
      }
      // streamProps.put(StreamsConfig.STATE_DIR_CONFIG, "/var/lib/")
      return streamProps;
   }

   

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public boolean dataSigningEnabled() {
      return getSecuritySvcsSignatureUri() != null && !StringUtils.isEmptyOrWhitespace(getSecuritySvcsSignatureUri())
            && !getSecuritySvcsSignatureUri().startsWith("UNSECURE");
   }

   public List<Path> getUploadLocations() {
      return this.uploadLocations;
   }

   public String getProperty(String key) {
      return env.getProperty(key);
   }

   public String getProperty(String key, String defaultValue) {
      return env.getProperty(key, defaultValue);
   }

   public Object getProperty(String key, int i) {
      return env.getProperty(key, Integer.class, i);
   }

   public String getHostId() {
      return hostId;
   }

   public String getPluginsLocations() {
      return pluginsLocations;
   }

   public void setPluginsLocations(String pluginsLocations) {
      this.pluginsLocations = pluginsLocations;
   }

   public String getKafkaBrokers() {
      return kafkaBrokers;
   }

   public void setKafkaBrokers(String kafkaBrokers) {
      this.kafkaBrokers = kafkaBrokers;
   }

   public String getKafkaProducerType() {
      return kafkaProducerType;
   }

   public void setKafkaProducerType(String kafkaProducerType) {
      this.kafkaProducerType = kafkaProducerType;
   }

   public Environment getEnv() {
      return env;
   }

   public void setEnv(Environment env) {
      this.env = env;
   }

   @Override
   public void setEnvironment(Environment environment) {
      env = environment;
   }

   public String getUploadLocationRoot() {
      return uploadLocationRoot;
   }

   public String getDdsCasPassword() {
      return ddsCasPass;
   }

   public void setDdsCasPassword(String ddsCasPass) {
      this.ddsCasPass = ddsCasPass;
   }

   public int getMessagesUntilTrustReestablished() {
      return messagesUntilTrustReestablished;
   }

   public void setMessagesUntilTrustReestablished(int messagesUntilTrustReestablished) {
      this.messagesUntilTrustReestablished = messagesUntilTrustReestablished;
   }

   public String getCaCertPath() {
      return caCertPath;
   }

   public void setCaCertPath(String caCertPath) {
      this.caCertPath = caCertPath;
   }

   public String getSelfCertPath() {
      return selfCertPath;
   }

   public void setSelfCertPath(String selfCertPath) {
      this.selfCertPath = selfCertPath;
   }

   public String getSelfPrivateKeyReconstructionFilePath() {
      return selfPrivateKeyReconstructionFilePath;
   }

   public void setSelfPrivateKeyReconstructionFilePath(String selfPrivateKeyReconstructionFilePath) {
      this.selfPrivateKeyReconstructionFilePath = selfPrivateKeyReconstructionFilePath;
   }

   public String getSelfSigningPrivateKeyFilePath() {
      return selfSigningPrivateKeyFilePath;
   }

   public void setSelfSigningPrivateKeyFilePath(String selfSigningPrivateKeyFilePath) {
      this.selfSigningPrivateKeyFilePath = selfSigningPrivateKeyFilePath;
   }

   public Boolean getVerboseJson() {
      return verboseJson;
   }

   public void setVerboseJson(Boolean verboseJson) {
      this.verboseJson = verboseJson;
   }

   public int getBsmReceiverPort() {
      return bsmReceiverPort;
   }

   public void setBsmReceiverPort(int bsmReceiverPort) {
      this.bsmReceiverPort = bsmReceiverPort;
   }

   public int getBsmBufferSize() {
      return bsmBufferSize;
   }

   public void setBsmBufferSize(int bsmBufferSize) {
      this.bsmBufferSize = bsmBufferSize;
   }

   public int getTimReceiverPort() {
      return timReceiverPort;
   }

   public void setTimReceiverPort(int timReceiverPort) {
      this.timReceiverPort = timReceiverPort;
   }

   public int getTimBufferSize() {
      return timBufferSize;
   }

   public void setTimBufferSize(int timBufferSize) {
      this.timBufferSize = timBufferSize;
   }

   public int getSsmReceiverPort() {
      return ssmReceiverPort;
   }

   public void setSsmReceiverPort(int ssmReceiverPort) {
      this.ssmReceiverPort = ssmReceiverPort;
   }

   public int getSsmBufferSize() {
      return ssmBufferSize;
   }

   public void setSsmBufferSize(int ssmBufferSize) {
      this.ssmBufferSize = ssmBufferSize;
   }

   public int getSrmReceiverPort() {
      return srmReceiverPort;
   }

   public void setSrmReceiverPort(int srmReceiverPort) {
      this.srmReceiverPort = srmReceiverPort;
   }

   public int getSrmBufferSize() {
      return srmBufferSize;
   }

   public void setSrmBufferSize(int srmBufferSize) {
      this.srmBufferSize = srmBufferSize;
   }

   public int getSpatReceiverPort() {
      return spatReceiverPort;
   }

   public void setSpatReceiverPort(int spatReceiverPort) {
      this.spatReceiverPort = spatReceiverPort;
   }

   public int getSpatBufferSize() {
      return spatBufferSize;
   }

   public void setSpatBufferSize(int spatBufferSize) {
      this.spatBufferSize = spatBufferSize;
   }

   public int getMapReceiverPort() {
      return mapReceiverPort;
   }

   public void setMapReceiverPort(int mapReceiverPort) {
      this.mapReceiverPort = mapReceiverPort;
   }

   public int getMapBufferSize() {
      return mapBufferSize;
   }

   public void setMapBufferSize(int mapBufferSize) {
      this.mapBufferSize = mapBufferSize;
   }

   public String getDdsCasUrl() {
      return ddsCasUrl;
   }

   public void setDdsCasUrl(String ddsCasUrl) {
      this.ddsCasUrl = ddsCasUrl;
   }

   public String getDdsCasUsername() {
      return ddsCasUsername;
   }

   public void setDdsCasUsername(String ddsCasUsername) {
      this.ddsCasUsername = ddsCasUsername;
   }

   public String getDdsWebsocketUrl() {
      return ddsWebsocketUrl;
   }

   public void setDdsWebsocketUrl(String ddsWebsocketUrl) {
      this.ddsWebsocketUrl = ddsWebsocketUrl;
   }

   public void setUploadLocationRoot(String uploadLocationRoot) {
      this.uploadLocationRoot = uploadLocationRoot;
   }

   public int getRsuSrmSlots() {
      return rsuSrmSlots;
   }

   public void setRsuSrmSlots(int rsuSrmSlots) {
      this.rsuSrmSlots = rsuSrmSlots;
   }

   public int getTrustRetries() {
      return trustRetries;
   }

   public void setTrustRetries(int trustRetries) {
      this.trustRetries = trustRetries;
   }

   public static byte[] getJpoOdeGroupId() {
      return JPO_ODE_GROUP_ID;
   }

   public int getImportProcessorBufferSize() {
      return importProcessorBufferSize;
   }

   public void setImportProcessorBufferSize(int importProcessorBufferSize) {
      this.importProcessorBufferSize = importProcessorBufferSize;
   }

   public String[] getKafkaTopicsDisabled() {
      return kafkaTopicsDisabled;
   }

   public void setKafkaTopicsDisabled(String[] kafkaTopicsDisabled) {
      this.kafkaTopicsDisabled = kafkaTopicsDisabled;
   }

   public Set<String> getKafkaTopicsDisabledSet() {
      return kafkaTopicsDisabledSet;
   }

   public void setKafkaTopicsDisabledSet(Set<String> kafkaTopicsDisabledSet) {
      this.kafkaTopicsDisabledSet = kafkaTopicsDisabledSet;
   }

   public String getKafkaTopicFilteredOdeBsmJson() {
      return kafkaTopicFilteredOdeBsmJson;
   }

   public void setKafkaTopicFilteredOdeBsmJson(String kafkaTopicFilteredOdeBsmJson) {
      this.kafkaTopicFilteredOdeBsmJson = kafkaTopicFilteredOdeBsmJson;
   }

   public String getKafkaTopicOdeBsmPojo() {
      return kafkaTopicOdeBsmPojo;
   }

   public void setKafkaTopicOdeBsmPojo(String kafkaTopicOdeBsmPojo) {
      this.kafkaTopicOdeBsmPojo = kafkaTopicOdeBsmPojo;
   }

   public String getKafkaTopicOdeBsmJson() {
      return kafkaTopicOdeBsmJson;
   }

   public void setKafkaTopicOdeBsmJson(String kafkaTopicOdeBsmJson) {
      this.kafkaTopicOdeBsmJson = kafkaTopicOdeBsmJson;
   }

   public String getKafkaTopicBsmRepartition() {
      return kafkaTopicBsmRepartition;
   }

   public void setKafkaTopicBsmRepartition(String kafkaTopicBsmRepartition) {
      this.kafkaTopicBsmRepartition = kafkaTopicBsmRepartition;
   }

   public String getKafkaTopicCmBsmEvent() {
      return kafkaTopicCmBsmEvent;
   }

   public void setKafkaTopicCmBsmEvent(String kafkaTopicCmBsmEvent) {
      this.kafkaTopicCmBsmEvent = kafkaTopicCmBsmEvent;
   }

   public String getKafkaTopicAsn1DecoderInput() {
      return kafkaTopicAsn1DecoderInput;
   }

   public void setKafkaTopicAsn1DecoderInput(String kafkaTopicAsn1DecoderInput) {
      this.kafkaTopicAsn1DecoderInput = kafkaTopicAsn1DecoderInput;
   }

   public String getKafkaTopicAsn1DecoderOutput() {
      return kafkaTopicAsn1DecoderOutput;
   }

   public void setKafkaTopicAsn1DecoderOutput(String kafkaTopicAsn1DecoderOutput) {
      this.kafkaTopicAsn1DecoderOutput = kafkaTopicAsn1DecoderOutput;
   }

   public String getKafkaTopicAsn1EncoderInput() {
      return kafkaTopicAsn1EncoderInput;
   }

   public void setKafkaTopicAsn1EncoderInput(String kafkaTopicAsn1EncoderInput) {
      this.kafkaTopicAsn1EncoderInput = kafkaTopicAsn1EncoderInput;
   }

   public String getKafkaTopicAsn1EncoderOutput() {
      return kafkaTopicAsn1EncoderOutput;
   }

   public void setKafkaTopicAsn1EncoderOutput(String kafkaTopicAsn1EncoderOutput) {
      this.kafkaTopicAsn1EncoderOutput = kafkaTopicAsn1EncoderOutput;
   }

   public String getKafkaTopicOdeDNMsgJson() {
      return kafkaTopicOdeDNMsgJson;
   }

   public void setKafkaTopicOdeDNMsgJson(String kafkaTopicOdeDNMsgJson) {
      this.kafkaTopicOdeDNMsgJson = kafkaTopicOdeDNMsgJson;
   }

   public String getKafkaTopicOdeTimJson() {
      return kafkaTopicOdeTimJson;
   }

   public void setKafkaTopicOdeTimJson(String kafkaTopicOdeTimJson) {
      this.kafkaTopicOdeTimJson = kafkaTopicOdeTimJson;
   }

   public String getUploadLocationObuLog() {
      return uploadLocationObuLogLog;
   }

   public void setUploadLocationObuLog(String uploadLocationObuLog) {
      this.uploadLocationObuLogLog = uploadLocationObuLog;
   }

   public String getKafkaTopicOdeBsmDuringEventPojo() {
      return kafkaTopicOdeBsmDuringEventPojo;
   }

   public void setKafkaTopicOdeBsmDuringEventPojo(String kafkaTopicOdeBsmDuringEventPojo) {
      this.kafkaTopicOdeBsmDuringEventPojo = kafkaTopicOdeBsmDuringEventPojo;
   }

   public String getKafkaTopicOdeBsmRxPojo() {
      return kafkaTopicOdeBsmRxPojo;
   }

   public void setKafkaTopicOdeBsmRxPojo(String kafkaTopicOdeBsmRxPojo) {
      this.kafkaTopicOdeBsmRxPojo = kafkaTopicOdeBsmRxPojo;
   }

   public String getKafkaTopicOdeBsmTxPojo() {
      return kafkaTopicOdeBsmTxPojo;
   }

   public void setKafkaTopicOdeBsmTxPojo(String kafkaTopicOdeBsmTxPojo) {
      this.kafkaTopicOdeBsmTxPojo = kafkaTopicOdeBsmTxPojo;
   }

   public String getKafkaTopicOdeTimRxJson() {
      return kafkaTopicOdeTimRxJson;
   }

   public void setKafkaTopicOdeTimRxJson(String kafkaTopicOdeTimRxJson) {
      this.kafkaTopicOdeTimRxJson = kafkaTopicOdeTimRxJson;
   }

   public String getKafkaTopicOdeTimBroadcastPojo() {
      return kafkaTopicOdeTimBroadcastPojo;
   }

   public void setKafkaTopicOdeTimBroadcastPojo(String kafkaTopicOdeTimBroadcastPojo) {
      this.kafkaTopicOdeTimBroadcastPojo = kafkaTopicOdeTimBroadcastPojo;
   }

   public String getKafkaTopicOdeTimBroadcastJson() {
      return kafkaTopicOdeTimBroadcastJson;
   }

   public void setKafkaTopicOdeTimBroadcastJson(String kafkaTopicOdeTimBroadcastJson) {
      this.kafkaTopicOdeTimBroadcastJson = kafkaTopicOdeTimBroadcastJson;
   }

   public String getKafkaTopicJ2735TimBroadcastJson() {
      return kafkaTopicJ2735TimBroadcastJson;
   }

   public void setKafkaTopicJ2735TimBroadcastJson(String kafkaTopicJ2735TimBroadcastJson) {
      this.kafkaTopicJ2735TimBroadcastJson = kafkaTopicJ2735TimBroadcastJson;
   }

   public String getKafkaTopicFilteredOdeTimJson() {
      return kafkaTopicFilteredOdeTimJson;
   }

   public void setKafkaTopicFilteredOdeTimJson(String kafkaTopicFilteredOdeTimJson) {
      this.kafkaTopicFilteredOdeTimJson = kafkaTopicFilteredOdeTimJson;
   }

   public String getKafkaTopicDriverAlertJson() {
      return kafkaTopicDriverAlertJson;
   }

   public void setKafkaTopicDriverAlertJson(String kafkaTopicDriverAlertJson) {
      this.kafkaTopicDriverAlertJson = kafkaTopicDriverAlertJson;
   }

   public String getKafkaTopicCmVehicleEvent() {
      return kafkaTopicCmVehicleEvent;
   }

   public void setKafkaTopicCmVehicleEvent(String kafkaTopicVehicleEvent) {
      this.kafkaTopicCmVehicleEvent = kafkaTopicVehicleEvent;
   }

   public String getKafakTopicCmVehicleStopEvent() {
      return kafakTopicCmVehicleStopEvent;
   }

   public void setKafakTopicCmVehicleStopEvent(String kafakTopicCmVehicleStopEvent) {
      this.kafakTopicCmVehicleStopEvent = kafakTopicCmVehicleStopEvent;
   }

   public String getKafkatopicCmLaneDirectionOfTravelEvent() {
      return kafkatopicCmLaneDirectionOfTravelEvent;
   }

   public void setKafkatopicCmLaneDirectionOfTravelEvent(String kafkatopicCmLaneDirectionOfTravelEvent) {
      this.kafkatopicCmLaneDirectionOfTravelEvent = kafkatopicCmLaneDirectionOfTravelEvent;
   }

   public String getKafkaTopicCmConnectionOfTravelEvent() {
      return kafkaTopicCmConnectionOfTravelEvent;
   }

   public void setKafkaTopicCmConnectionOfTravelEvent(String kafkaTopicCmConnectionOfTravelEvent) {
      this.kafkaTopicCmConnectionOfTravelEvent = kafkaTopicCmConnectionOfTravelEvent;
   }

   public String getKafkaTopicCmSignalStateEvent() {
      return kafkaTopicCmSignalStateEvent;
   }

   public void setKafkaTopicCmSignalStateEvent(String kafkaTopicCmSignalStateEvent) {
      this.kafkaTopicCmSignalStateEvent = kafkaTopicCmSignalStateEvent;
   }

   public Integer getFileWatcherPeriod() {
      return fileWatcherPeriod;
   }

   public void setFileWatcherPeriod(Integer fileWatcherPeriod) {
      this.fileWatcherPeriod = fileWatcherPeriod;
   }

   public String getSecuritySvcsSignatureUri() {
      return securitySvcsSignatureUri;
   }

   public void setSecuritySvcsSignatureUri(String securitySvcsSignatureUri) {
      this.securitySvcsSignatureUri = securitySvcsSignatureUri;
   }

   public String getRsuUsername() {
      return rsuUsername;
   }

   public void setRsuUsername(String rsuUsername) {
      this.rsuUsername = rsuUsername;
   }

   public String getRsuPassword() {
      return rsuPassword;
   }

   public void setRsuPassword(String rsuPassword) {
      this.rsuPassword = rsuPassword;
   }

   public String getKafkaTopicSdwDepositorInput() {
      return kafkaTopicSdwDepositorInput;
   }

   public void setKafkaTopicSdwDepositorInput(String kafkaTopicSdwDepositorInput) {
      this.kafkaTopicSdwDepositorInput = kafkaTopicSdwDepositorInput;
   }

   public boolean shouldDepositSdwMessagesOverWebsocket() {
      return depositSdwMessagesOverWebsocket;
   }

   public void setDepositSdwMessagesOverWebsocket(boolean depositSdwMessagesOverWebsocket) {
      this.depositSdwMessagesOverWebsocket = depositSdwMessagesOverWebsocket;
   }

   public String getKafkaTopicSignedOdeTimJsonExpiration() {
      return kafkaTopicSignedOdeTimJsonExpiration;
   }

   public void setKafkaTopicSignedOdeTimJsonExpiration(String kafkaTopicSignedOdeTimJsonExpiration) {
      this.kafkaTopicSignedOdeTimJsonExpiration = kafkaTopicSignedOdeTimJsonExpiration;
   }

   public String getKafkaTopicOdeSpatTxPojo() {
      return kafkaTopicOdeSpatTxPojo;
   }

   public void setKafkaTopicOdeSpatTxPojo(String kafkaTopicOdeSpatTxPojo) {
      this.kafkaTopicOdeSpatTxPojo = kafkaTopicOdeSpatTxPojo;
   }

   public String getKafkaTopicOdeSpatPojo() {
      return kafkaTopicOdeSpatPojo;
   }

   public void setKafkaTopicOdeSpatPojo(String kafkaTopicOdeSpatPojo) {
      this.kafkaTopicOdeSpatPojo = kafkaTopicOdeSpatPojo;
   }

   public String getKafkaTopicOdeSpatJson() {
      return kafkaTopicOdeSpatJson;
   }

   public void setKafkaTopicOdeSpatJson(String kafkaTopicOdeSpatJson) {
      this.kafkaTopicOdeSpatJson = kafkaTopicOdeSpatJson;
   }

   public String getKafkaTopicOdeSpatRxPojo() {
      return kafkaTopicOdeSpatRxPojo;
   }

   public void setKafkaTopicOdeSpatRxPojo(String kafkaTopicOdeSpatRxPojo) {
      this.kafkaTopicOdeSpatRxPojo = kafkaTopicOdeSpatRxPojo;
   }

   public String getKafkaTopicOdeSpatRxJson() {
      return kafkaTopicOdeSpatRxJson;
   }

   public void setKafkaTopicOdeSpatRxJson(String kafkaTopicOdeSpatRxJson) {
      this.kafkaTopicOdeSpatRxJson = kafkaTopicOdeSpatRxJson;
   }

   public String getKafkaTopicFilteredOdeSpatJson() {
      return kafkaTopicFilteredOdeSpatJson;
   }

   public void setKafkaTopicFilteredOdeSpatJson(String kafkaTopicFilteredOdeSpatJson) {
      this.kafkaTopicFilteredOdeSpatJson = kafkaTopicFilteredOdeSpatJson;
   }

   public String getKafkaTopicSpatGeoJson() {
      return kafkaTopicSpatGeoJson;
   }

   public void setKafkaTopicSpatGeoJson(String kafkaTopicSpatGeoJson) {
      this.kafkaTopicSpatGeoJson = kafkaTopicSpatGeoJson;
   }

   public String getKafkaTopicOdeRawEncodedBSMJson() {
      return kafkaTopicOdeRawEncodedBSMJson;
   }

   public void setKafkaTopicOdeRawEncodedBSMJson(String kafkaTopicOdeRawEncodedBSMJson) {
      this.kafkaTopicOdeRawEncodedBSMJson = kafkaTopicOdeRawEncodedBSMJson;
   }

   public String getKafkaTopicOdeRawEncodedTIMJson() {
      return kafkaTopicOdeRawEncodedTIMJson;
   }

   public void setKafkaTopicOdeRawEncodedTIMJson(String kafkaTopicOdeRawEncodedTIMJson) {
      this.kafkaTopicOdeRawEncodedTIMJson = kafkaTopicOdeRawEncodedTIMJson;
   }

   public String getKafkaTopicOdeRawEncodedSPATJson() {
      return kafkaTopicOdeRawEncodedSPATJson;
   }

   public void setKafkaTopicOdeRawEncodedSPATJson(String kafkaTopicOdeRawEncodedSPATJson) {
      this.kafkaTopicOdeRawEncodedSPATJson = kafkaTopicOdeRawEncodedSPATJson;
   }

   public String getKafkaTopicProcessedSpat() {
      return kafkaTopicProcessedSpat;
   }

   public void setKafkaTopicProcessedSpat(String kafkaTopicProcessedSpat) {
      this.kafkaTopicProcessedSpat = kafkaTopicProcessedSpat;
   }

   public String getKafkaTopicOdeRawEncodedMAPJson() {
      return kafkaTopicOdeRawEncodedMAPJson;
   }

   public void setKafkaTopicOdeRawEncodedMAPJson(String kafkaTopicOdeRawEncodedMAPJson) {
      this.kafkaTopicOdeRawEncodedMAPJson = kafkaTopicOdeRawEncodedMAPJson;
   }

   public String getKafkaTopicOdeMapTxPojo() {
      return kafkaTopicOdeMapTxPojo;
   }

   public void setKafkaTopicOdeMapTxPojo(String kafkaTopicOdeMapTxPojo) {
      this.kafkaTopicOdeMapTxPojo = kafkaTopicOdeMapTxPojo;
   }

   public String getKafkaTopicOdeMapJson() {
      return kafkaTopicOdeMapJson;
   }

   public void setKafkaTopicOdeMapJson(String kafkaTopicOdeMapJson) {
      this.kafkaTopicOdeMapJson = kafkaTopicOdeMapJson;
   }

   public String getKafkaTopicMapGeoJson() {
      return kafkaTopicMapGeoJson;
   }

   public void setKafkaTopicMapGeoJson(String kafkaTopicMapGeoJson) {
      this.kafkaTopicMapGeoJson = kafkaTopicMapGeoJson;
   }

   public String getKafkaTopicProcessedMap() {
      return kafkaTopicProcessedMap;
   }

   public void setKafkaTopicProcessedMap(String kafkaTopicProcessedMap) {
      this.kafkaTopicProcessedMap = kafkaTopicProcessedMap;
   }

   public String getKafkaTopicOdeRawEncodedSSMJson() {
      return kafkaTopicOdeRawEncodedSSMJson;
   }

   public void setKafkaTopicOdeRawEncodedSSMJson(String kafkaTopicOdeRawEncodedSSMJson) {
      this.kafkaTopicOdeRawEncodedSSMJson = kafkaTopicOdeRawEncodedSSMJson;
   }

   public String getKafkaTopicOdeSsmPojo() {
      return kafkaTopicOdeSsmPojo;
   }

   public void setKafkaTopicOdeSsmPojo(String kafkaTopicOdeSsmPojo) {
      this.kafkaTopicOdeSsmPojo = kafkaTopicOdeSsmPojo;
   }

   public String getKafkaTopicOdeSsmJson() {
      return kafkaTopicOdeSsmJson;
   }

   public void setKafkaTopicOdeSsmJson(String kafkaTopicOdeSsmJson) {
      this.kafkaTopicOdeSsmJson = kafkaTopicOdeSsmJson;
   }

   public String getKafkaTopicOdeRawEncodedSRMJson() {
      return kafkaTopicOdeRawEncodedSRMJson;
   }

   public void setKafkaTopicOdeRawEncodedSRMJson(String kafkaTopicOdeRawEncodedSRMJson) {
      this.kafkaTopicOdeRawEncodedSRMJson = kafkaTopicOdeRawEncodedSRMJson;
   }

   public String getKafkaTopicOdeSrmTxPojo() {
      return kafkaTopicOdeSrmTxPojo;
   }

   public void setKafkaTopicOdeSrmTxPojo(String kafkaTopicOdeSrmTxPojo) {
      this.kafkaTopicOdeSrmTxPojo = kafkaTopicOdeSrmTxPojo;
   }

   public String getKafkaTopicOdeSrmJson() {
      return kafkaTopicOdeSrmJson;
   }

   public void setKafkaTopicOdeSrmJson(String kafkaTopicOdeSrmJson) {
      this.kafkaTopicOdeSrmJson = kafkaTopicOdeSrmJson;
   }

}
