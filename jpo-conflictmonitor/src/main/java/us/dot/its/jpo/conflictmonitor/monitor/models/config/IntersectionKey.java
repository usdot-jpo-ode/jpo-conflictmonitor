package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import javax.annotation.Nullable;
import java.util.Objects;

@Getter
@Setter
public class IntersectionKey implements Comparable<IntersectionKey> {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionKey.class);

    /**
     * Optional.  Set to 0 if undefined.
     */
    int roadRegulatorID;

    /**
     * Required
     */
    int intersectionID;

    public IntersectionKey() {}

    public IntersectionKey(int roadRegulatorID, int intersectionID) {
        this.roadRegulatorID = roadRegulatorID;
        this.intersectionID = intersectionID;
    }

    public IntersectionKey(int intersectionID) {
        this.roadRegulatorID = 0;
        this.intersectionID = intersectionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof IntersectionKey)) return false;
        IntersectionKey otherKey = (IntersectionKey) o;

        // Compare region only if it is defined for both objects
        if (roadRegulatorID > 0 && otherKey.getRoadRegulatorID() > 0) {
            if (roadRegulatorID != otherKey.getRoadRegulatorID()) {
                return false;
            }
        }

        return intersectionID == otherKey.intersectionID;
    }

    @Override
    public int hashCode() {
        // hash code doesn't depend on region/ regulator ID
        return Objects.hash(intersectionID);
    }

    @Override
    public int compareTo(@Nullable IntersectionKey other) {
        if (other == null) return 1;

        // Compare region only if it is defined for both objects
        if (roadRegulatorID > 0 && other.getRoadRegulatorID() > 0) {
            int compareRegion = Integer.compare(roadRegulatorID, other.getRoadRegulatorID());
            if (compareRegion != 0) {
                return compareRegion;
            }
        }

        return Integer.compare(intersectionID, other.getIntersectionID());
    }

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
}
