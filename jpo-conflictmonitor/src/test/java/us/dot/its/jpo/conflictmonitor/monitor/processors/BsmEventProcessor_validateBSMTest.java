package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.util.ArrayList;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils.validProcessedBsm;

/**
 * Unit tests for {@link BsmEventProcessor#validateBSM}
 */
@RunWith(Parameterized.class)
@Slf4j
public class BsmEventProcessor_validateBSMTest {
    
    @Test
    public void testValidateBSM() {
        log.info("{}:", description);
        var actual = BsmEventProcessor.validateBSM(bsm);
        assertThat(description, actual, equalTo(expected));
    }

    private final String description;
    private final ProcessedBsm<Point> bsm;
    private final boolean expected;

    public BsmEventProcessor_validateBSMTest(String description, ProcessedBsm<Point> bsm, boolean expected) {
        this.description = description;
        this.bsm = bsm;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{index}: {0}, expected: {2}")
    public static Collection<Object[]> getParams() {

        var bsms = new ArrayList<Object[]>();

        bsms.add(new Object[] {"null BSM", null, false});

        final ProcessedBsm<Point> emptyBsm = new ProcessedBsm<Point>(null, null, null);
        bsms.add(new Object[] {"empty BSM", emptyBsm, false});


        final ProcessedBsm<Point> validBsm = validProcessedBsm();
        bsms.add(new Object[] {"Valid BSM", validBsm, true});

        final ProcessedBsm<Point> bsmWithNullId = validProcessedBsm();
        bsmWithNullId.getProperties().setId(null);
        bsms.add(new Object[] {"BSM with null id", bsmWithNullId, false});

        final ProcessedBsm<Point> bsmWithNullSecMark = validProcessedBsm();
        bsmWithNullSecMark.getProperties().setSecMark(null);
        bsms.add(new Object[] {"BSM with null secMark", bsmWithNullSecMark, false});

        final ProcessedBsm<Point> bsmWithNullSpeed = validProcessedBsm();
        bsmWithNullSpeed.getProperties().setSpeed(null);
        bsms.add(new Object[] {"BSM with null speed", bsmWithNullSpeed, false});

        final ProcessedBsm<Point> bsmWithNullHeading = validProcessedBsm();
        bsmWithNullHeading.getProperties().setHeading(null);
        bsms.add(new Object[] {"BSM with null heading", bsmWithNullHeading, false});

        final ProcessedBsm<Point> bsmWithNullSource = validProcessedBsm();
        bsmWithNullSource.getProperties().setOriginIp(null);
        bsmWithNullSource.getProperties().setLogName(null);
        bsms.add(new Object[] {"BSM with null source", bsmWithNullSource, false});


        final ProcessedBsm<Point> bsmWithNullRecordGeneratedAt = validProcessedBsm();
        bsmWithNullRecordGeneratedAt.getProperties().setTimeStamp(null);
        bsms.add(new Object[] {"BSM with null recordGeneratedAt", bsmWithNullRecordGeneratedAt, false});

        final ProcessedBsm<Point> bsmWithNullOdeReceivedAt = validProcessedBsm();
        bsmWithNullOdeReceivedAt.getProperties().setOdeReceivedAt(null);
        bsms.add(new Object[] {"BSM with null odeReceivedAt", bsmWithNullOdeReceivedAt, false});


        return bsms;
    }



    
}
