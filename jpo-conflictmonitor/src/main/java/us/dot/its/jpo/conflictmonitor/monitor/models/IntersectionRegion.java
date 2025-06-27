package us.dot.its.jpo.conflictmonitor.monitor.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents a unique intersection region, identified by intersection ID and region code.
 * Used for grouping and identifying intersections in the Conflict Monitor system.
 * Provides comparison, equality, and serialization utilities.
 */
@Getter
@Setter
public class IntersectionRegion implements Comparable<IntersectionRegion> {

    /** Logger for IntersectionRegion operations. */
    private static final Logger logger = LoggerFactory.getLogger(IntersectionRegion.class);

    /**
     * Default constructor. Initializes intersectionId and region to -1.
     */
    public IntersectionRegion() {}

    /**
     * Constructs an IntersectionRegion with the specified intersection ID and region.
     * If either value is null or negative, it is set to -1.
     *
     * @param intersectionId the intersection ID
     * @param region the region code
     */
    public IntersectionRegion(Integer intersectionId, Integer region) {
        if (intersectionId != null && intersectionId.intValue() >= 0) {
            this.intersectionId = intersectionId;
        } else {
            this.intersectionId = -1;
        }
        if (region != null && region.intValue() >= 0) {
            this.region = region;
        } else {
            this.region = -1;
        }
    }

    /**
     * Constructs an IntersectionRegion with the specified intersection ID and region set to -1.
     * If intersectionId is null or negative, it is set to -1.
     *
     * @param intersectionId the intersection ID
     */
    public IntersectionRegion(Integer intersectionId) {
        if (intersectionId != null && intersectionId.intValue() >= 0) {
            this.intersectionId = intersectionId;
        } else {
            this.intersectionId = -1;
        }
        this.region = -1;
    }

    /**
     * Constructs an IntersectionRegion from a ProcessedMap object.
     * Extracts intersection ID and region from the map's properties.
     *
     * @param map the processed map containing intersection properties
     */
    public IntersectionRegion(ProcessedMap<?> map) {
        if (map == null || map.getProperties() == null) return;
        this.intersectionId = map.getProperties().getIntersectionId();
        this.region = map.getProperties().getRegion();
    }

    /** The intersection ID. */
    protected int intersectionId;
    /** The region code. */
    protected int region;

    /**
     * Checks equality with another object.
     * Two IntersectionRegions are equal if their intersection IDs match,
     * and their regions match if both are defined (>0).
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof IntersectionRegion)) return false;
        IntersectionRegion otherKey = (IntersectionRegion) o;

        // Compare region only if it is defined for both objects
        if (region > 0 && otherKey.getRegion() > 0) {
            if (region != otherKey.getRegion()) {
                return false;
            }
        }

        return intersectionId == otherKey.intersectionId;
    }

    /**
     * Returns the hash code for this IntersectionRegion.
     * Only intersectionId is used for hashing.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        // hash code doesn't depend on region/ regulator ID
        return Objects.hash(intersectionId);
    }

    /**
     * Compares this IntersectionRegion to another for ordering.
     * Regions are compared if both are defined (>0), otherwise intersection IDs are compared.
     *
     * @param other the other IntersectionRegion to compare
     * @return comparison result
     */
    @Override
    public int compareTo(@Nullable IntersectionRegion other) {
        if (other == null) return 1;

        // Compare region only if it is defined for both objects
        if (region > 0 && other.getRegion() > 0) {
            int compareRegion = Integer.compare(region, other.getRegion());
            if (compareRegion != 0) {
                return compareRegion;
            }
        }

        return Integer.compare(intersectionId, other.getIntersectionId());
    }

    /**
     * Returns a JSON string representation of this IntersectionRegion.
     *
     * @return JSON string or empty string if serialization fails
     */
    @Override
    public String toString() {
        var mapper = DateJsonMapper.getInstance();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error("Error converting IntersectionKey to JSON", e);
            return "";
        }
    }

    /**
     * Creates an IntersectionRegion from an RsuIntersectionKey.
     *
     * @param rsuIntersectionKey the RSU intersection key
     * @return IntersectionRegion with the same intersection ID and region
     */
    public static IntersectionRegion fromRsuIntersectionKey(RsuIntersectionKey rsuIntersectionKey) {
        return new IntersectionRegion(rsuIntersectionKey.getIntersectionId(), rsuIntersectionKey.getRegion());
    }
}
