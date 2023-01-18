package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmEvent {
    private OdeBsmData startingBsm;
    private OdeBsmData endingBsm;
    private Long startingBsmTimestamp;
    private Long endingBsmTimestamp;

    public BsmEvent(OdeBsmData startingBsm){
        this.startingBsm = startingBsm;
    }

    public BsmEvent(OdeBsmData startingBsm, OdeBsmData endingBsm){
        this.startingBsm = startingBsm;
        this.endingBsm = endingBsm;
    }

    public OdeBsmData getStartingBsm() {
        return startingBsm;
    }

    public void setStartingBsm(OdeBsmData startingBsm) {
        this.startingBsm = startingBsm;
    }

    public OdeBsmData getEndingBsm() {
        return endingBsm;
    }

    public void setEndingBsm(OdeBsmData endingBsm) {
        this.endingBsm = endingBsm;
    }

    public void setStartingBsmTimestamp(Long startingBsmTimestamp){
        this.startingBsmTimestamp = startingBsmTimestamp;
    }

    public Long getStartingBsmTimestamp(){
        return startingBsmTimestamp;
    }

    public void setEndingBsmTimestamp(Long endingBsmTimestamp){
        this.endingBsmTimestamp = endingBsmTimestamp;
    }
    
    public Long getEndingBsmTimestamp(){
        return endingBsmTimestamp;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return testReturn;
    }
}
