package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.IntersectionReferenceAlignmentAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalGroupAlignmentAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationAlgorithm;

/**
 * Interface for MAP SPaT message assessment Algorithm validation algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for MAP SPaT message assessment algorithm parameters.
 */
public interface MapSpatMessageAssessmentAlgorithm extends Algorithm<MapSpatMessageAssessmentParameters>{

    /**
     * Sets the intersection reference alignment aggregation algorithm used MAP / SPaT assessments.
     *
     * @param intersectionReferenceAlignmentAggregationAlgorithm the intersection Reference Alignment Aggregation Algorithm to set
     */
    void setIntersectionReferenceAlignmentAggregationAlgorithm(IntersectionReferenceAlignmentAggregationAlgorithm intersectionReferenceAlignmentAggregationAlgorithm);
    
    /**
     * Sets the signal group alignment aggregation algorithm used MAP / SPaT assessments.
     *
     * @param signalGroupAlignmentAggregationAlgorithm the signal group alignment aggregation algorithm to set
     */
    void setSignalGroupAlignmentAggregationAlgorithm(SignalGroupAlignmentAggregationAlgorithm signalGroupAlignmentAggregationAlgorithm);
    
    /**
     * Sets the signal state conflict aggregation algorithm used MAP / SPaT assessments.
     *
     * @param signalStateConflictAggregationAlgorithm the signal state conflict aggregation algorithm to set
     */
    void setSignalStateConflictAggregationAlgorithm(SignalStateConflictAggregationAlgorithm signalStateConflictAggregationAlgorithm);

}







