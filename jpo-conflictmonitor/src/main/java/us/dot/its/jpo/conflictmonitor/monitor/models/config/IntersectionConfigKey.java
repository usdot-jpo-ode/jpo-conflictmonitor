package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import java.util.Objects;

@Getter
@Setter
public class IntersectionConfigKey implements IntersectionKey, Comparable<IntersectionConfigKey> {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionConfigKey.class);

    /**
     * Optional
     */
    int roadRegulatorID;

    /**
     * Required
     */
    int intersectionID;

    /**
     * Required
     */
    String key;

    public IntersectionConfigKey() {}

    public IntersectionConfigKey(int roadRegulatorID, int intersectionId, String key) {
        this.roadRegulatorID = roadRegulatorID;
        this.intersectionID = intersectionId;
        this.key = key;
    }

    public IntersectionConfigKey(int intersectionId, String key) {
        this.roadRegulatorID = 0;
        this.intersectionID = intersectionId;
        this.key = key;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntersectionConfigKey that = (IntersectionConfigKey) o;

        // Compare region only if it is defined for both objects
        if (roadRegulatorID > 0 && that.getRoadRegulatorID() > 0) {
            if (roadRegulatorID != that.getRoadRegulatorID()) {
                return false;
            }
        }

        return intersectionID == that.intersectionID && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intersectionID, key);
    }

    @Override
    public int compareTo(IntersectionConfigKey other) {
        if (other == null) return 1;

        // Compare region only if it is defined for both objects
        if (roadRegulatorID > 0 && other.getRoadRegulatorID() > 0) {
            int compareRegion = Integer.compare(roadRegulatorID, other.getRoadRegulatorID());
            if (compareRegion != 0) {
                return compareRegion;
            }
        }

        int compareIntersection = Integer.compare(intersectionID, other.getIntersectionID());
        if (compareIntersection != 0) {
            return compareIntersection;
        }
        return ObjectUtils.compare(key, other.getKey());

    }

    @Override
    public String toString() {
        var mapper = DateJsonMapper.getInstance();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error("Error converting IntersectionConfigKey to JSON", e);
            return "";
        }
    }


}
