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

@Getter
@Setter
public class IntersectionRegion implements Comparable<IntersectionRegion> {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionRegion.class);

    public IntersectionRegion() {}

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

    public IntersectionRegion(Integer intersectionId) {
        if (intersectionId != null && intersectionId.intValue() >= 0) {
            this.intersectionId = intersectionId;
        } else {
            this.intersectionId = -1;
        }
        this.region = -1;
    }

    public IntersectionRegion(ProcessedMap<?> map) {
        if (map == null || map.getProperties() == null) return;
        this.intersectionId = map.getProperties().getIntersectionId();
        this.region = map.getProperties().getRegion();
    }

    protected int intersectionId;
    protected int region;

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

    @Override
    public int hashCode() {
        // hash code doesn't depend on region/ regulator ID
        return Objects.hash(intersectionId);
    }

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


    public static IntersectionRegion fromRsuIntersectionKey(RsuIntersectionKey rsuIntersectionKey) {
        return new IntersectionRegion(rsuIntersectionKey.getIntersectionId(), rsuIntersectionKey.getRegion());
    }
}
