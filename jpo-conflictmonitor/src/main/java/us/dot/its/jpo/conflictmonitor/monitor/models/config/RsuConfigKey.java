package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import static org.apache.commons.lang3.StringUtils.rightPad;

import java.util.Objects;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;

/**
 * Kafka key for {@link IntersectionConfig} settings
 * that implements {@link us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey}
 */
@EqualsAndHashCode
public class RsuConfigKey implements RsuIdKey, Comparable<RsuConfigKey> {

    private static final Logger logger = LoggerFactory.getLogger(RsuConfigKey.class);

    @Setter String rsuId;

    @Getter @Setter String key;
   

    @Override
    public String getRsuId() {
        return rsuId;
    }

    
    public RsuConfigKey() {}


    public RsuConfigKey(String rsuId, String key) {
        this.rsuId = rsuId;
        this.key = key;
    }


    @Override
    public int compareTo(RsuConfigKey other) {
        if (other == null) return 1;
        int compareRsu = ObjectUtils.compare(rsuId, other.getRsuId());
        if (compareRsu != 0) {
            return compareRsu;
        } else {
            return ObjectUtils.compare(key, other.getKey());
        }
    }

    @Override
    public String toString() {
        var mapper = DateJsonMapper.getInstance();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error("Error converting RsuConfigKey to JSON", e);
            return "";
        }
    }


   
   
    
}


