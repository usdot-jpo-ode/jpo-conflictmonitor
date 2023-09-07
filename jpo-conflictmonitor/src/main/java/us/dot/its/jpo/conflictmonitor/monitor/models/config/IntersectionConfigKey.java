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
public class IntersectionConfigKey extends IntersectionKey  {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionConfigKey.class);

    /**
     * Required
     */
    String key;

    public IntersectionConfigKey() {}

    public IntersectionConfigKey(int roadRegulatorID, int intersectionID, String key) {
        super(roadRegulatorID, intersectionID);
        this.key = key;
    }

    public IntersectionConfigKey(int intersectionID, String key) {
        super(intersectionID);
        this.key = key;
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
        return Objects.hash(getIntersectionID(), key);
    }

    @Override
    public int compareTo(IntersectionKey other) {
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
