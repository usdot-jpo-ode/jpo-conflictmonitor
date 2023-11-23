package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionKey;

import javax.annotation.Nullable;
import java.util.Objects;

@Getter
@Setter
public class IntersectionConfigKey
        extends IntersectionRegion
        implements IntersectionKey {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionConfigKey.class);

    /**
     * Required
     */
    String key;

    public IntersectionConfigKey() {}

    public IntersectionConfigKey(int region, int intersectionId, String key) {
        super(region, intersectionId);
        this.key = key;
    }

    public IntersectionConfigKey(int intersectionId, String key) {
        super(intersectionId);
        this.region = -1;
        this.key = key;
    }


    @Override
    public int getIntersectionId() {
        return intersectionId;
    }

    @Override
    public int getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        boolean compareIntersection = super.equals(o);
        if (!compareIntersection) return false;
        if (!(o instanceof IntersectionConfigKey)) return false;
        return Objects.equals(key, ((IntersectionConfigKey)o).getKey());
    }

    @Override
    public int hashCode() {
        // Only include intersection ID and key, so that hash code doesn't depend on region
        return Objects.hash(intersectionId, key);
    }

    @Override
    public int compareTo(@Nullable IntersectionRegion other) {
        if (other == null) return 1;
        int compareIntersection = super.compareTo(other);
        if (compareIntersection != 0) return compareIntersection;
        if (!(other instanceof IntersectionConfigKey)) return -1;
        return ObjectUtils.compare(key, ((IntersectionConfigKey)other).getKey());
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
