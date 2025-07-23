package us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment;

import lombok.*;

/**
 * Holds a subset of the information from the J2735 LaneTypeAttribute
 * CHOICE type:
 * <ul>
 *     <li>The Type of lane: Vehicle, Crosswalk, Bike Lane, etc</li>
 *     <li>The revocable flag for the lane</li>
 * </ul>
 */
public record RevocableLaneTypeAttributes (
    String laneType,
    boolean revocable
) {}
