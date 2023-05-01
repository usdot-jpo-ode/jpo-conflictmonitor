package us.dot.its.jpo.conflictmonitor.testutils;

import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatTestUtils {

    public static ProcessedSpat validSpat(int intersectionId) {
        ProcessedSpat spat = new ProcessedSpat();
        spat.setIntersectionId(intersectionId);
        return spat;
    }
}
