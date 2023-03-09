package us.dot.its.jpo.ode.messagesender;

import java.util.Scanner;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.ode.messagesender.scriptrunner.ScriptRunner;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.converter.map.MapProcessedJsonConverter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.DeserializedRawMap;
import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;

@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/odeMapJsonToGeojson", consumes = "application/json", produces = "*/*")
    public @ResponseBody ResponseEntity<String> odeMapJsonToGeojson(@RequestBody OdeMapData odeMapData) {
        try {
            var processor = new MapProcessedJsonConverter();
            var rawMap = new DeserializedRawMap();
            rawMap.setOdeMapOdeMapData(odeMapData);
            rawMap.setValidatorResults(new JsonValidatorResult());
            var processedMap = processor.transform(null, rawMap).value;
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(processedMap.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                .body(ExceptionUtils.getStackTrace(e));
        }
    }

   

    @Autowired
    KafkaTemplate<String, String> template;

    @Autowired
    ScriptRunner scriptRunner;

    @PostMapping(value = "/kafka/{topic}", consumes = "*/*", produces = "*/*")
    public @ResponseBody ResponseEntity<String> kafka(@RequestBody String message, @PathVariable String topic) {
        try {
            var result = template.send(topic, message);
            SendResult<String, String> sendResult = result.completable().join();
            String strResult = sendResult.toString();
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(strResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                    .body(ExceptionUtils.getStackTrace(e));
        }

    }

    

    @PostMapping(value = "/script", consumes = "*/*", produces = "*/*")
    public @ResponseBody ResponseEntity<String> runScript(@RequestBody String script) {
        logger.info("runScript");
        try {
            scriptRunner.scheduleScript(script);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body("running script");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                    .body(ExceptionUtils.getStackTrace(e));
        }
    }
    
    @PostMapping(value = "/convertHexLogToScript", consumes = "*/*", produces = "*/*")
    public @ResponseBody ResponseEntity<String> convertHexLogToScript(@RequestBody String hexScript) {
        logger.info("convertHexLogToScript");
        var mapper = DateJsonMapper.getInstance();
        try {
            var hexLog = new HexLog();
            try (MappingIterator<HexLogItem> iterator = mapper.readerFor(HexLogItem.class).readValues(hexScript)) {
                while (iterator.hasNext()) {
                    hexLog.add(iterator.next());
                }
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(hexLog));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                    .body(ExceptionUtils.getStackTrace(e));
        }
    }
   
}
