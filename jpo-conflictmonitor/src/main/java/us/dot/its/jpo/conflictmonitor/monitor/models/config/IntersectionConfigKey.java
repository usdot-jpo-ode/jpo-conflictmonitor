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
public class IntersectionConfigKey implements Comparable<IntersectionConfigKey> {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionConfigKey.class);

    int intersectionId;
    int region;
    String key;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntersectionConfigKey that = (IntersectionConfigKey) o;

        // Compare region only if it is defined for both objects
        if (region > 0 && that.getRegion() > 0) {
            if (region != that.getRegion()) {
                return false;
            }
        }

        return intersectionId == that.intersectionId && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intersectionId, key);
    }

    @Override
    public int compareTo(IntersectionConfigKey other) {
        if (other == null) return 1;

        // Compare region only if it is defined for both objects
        if (region > 0 && other.getRegion() > 0) {
            int compareRegion = Integer.compare(region, other.getRegion());
            if (compareRegion != 0) {
                return compareRegion;
            }
        }

        int compareIntersection = Integer.compare(intersectionId, other.getIntersectionId());
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
