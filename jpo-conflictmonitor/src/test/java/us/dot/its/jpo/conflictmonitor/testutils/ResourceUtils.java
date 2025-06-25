package us.dot.its.jpo.conflictmonitor.testutils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResourceUtils {

    public static String loadResource(String path) {
        String str;
        try {
            str = IOUtils.resourceToString(path, StandardCharsets.UTF_8);
            log.debug("Loaded resource: {}", str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }
}
