package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.ode.model.OdeBsmData;

@Getter
@Setter
@EqualsAndHashCode
@Generated
public class BsmEvent {
    private OdeBsmData startingBsm;
    private OdeBsmData endingBsm;
    private Long startingBsmTimestamp;
    private Long endingBsmTimestamp;
    private String wktPath;
    private String wktMapBoundingBox;
    public boolean inMapBoundingBox;

    public BsmEvent() {}

    public BsmEvent(OdeBsmData startingBsm){
        this.startingBsm = startingBsm;
    }

    public BsmEvent(OdeBsmData startingBsm, OdeBsmData endingBsm){
        this.startingBsm = startingBsm;
        this.endingBsm = endingBsm;
    }

}
