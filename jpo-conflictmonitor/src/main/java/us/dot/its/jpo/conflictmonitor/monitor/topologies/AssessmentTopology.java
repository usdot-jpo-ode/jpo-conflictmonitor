package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment.AssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment.AssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.Assessment;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment.AssessmentConstants.DEFAULT_ASSESSMENT_ALGORITHM;


@Component(DEFAULT_ASSESSMENT_ALGORITHM)
public class AssessmentTopology
        extends BaseStreamsTopology<AssessmentParameters>
        implements AssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }



    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Assessment> cotStream = builder.stream(parameters.getConnectionOfTravelAssessmentTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Assessment()));
        KStream<String, Assessment> allAssessments = cotStream
            .merge(builder.stream(parameters.getLaneDirectionOfTravelAssessmentTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Assessment())))
            .merge(builder.stream(parameters.getStopLinePassageAssessmentTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Assessment())))
            .merge(builder.stream(parameters.getConnectionOfTravelAssessmentTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Assessment())));        

        allAssessments.to(parameters.getAssessmentOutputTopicName(), Produced.with(Serdes.String(), JsonSerdes.Assessment()));
        if(parameters.isDebug()){
            allAssessments.print(Printed.toSysOut());
        }
        


        return builder.build();

    }



}
