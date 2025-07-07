package us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;

/**
 * * Interface for an assessment algorithm that is implemented as a Kafka Streams
 */
public interface AssessmentStreamsAlgorithm
    extends AssessmentAlgorithm, StreamsTopology {}
