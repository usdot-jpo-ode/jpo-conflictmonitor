package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.ode.model.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils.validBsm;
import static us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils.validProcessedBsm;

/**
 * Unit tests for {@link BsmEventProcessor#validateBSM}
 */
@RunWith(Parameterized.class)
public class BsmEventProcessor_validateBSMTest {
    
    @Test
    public void testValidateBSM() {
        var processor = new BsmEventProcessor();
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

        final var emptyBsm = new ProcessedBsm<Point>(null);
        bsms.add(new Object[] {"empty BSM", emptyBsm, false});


        final OdeBsmData validBsm = validBsm();
        bsms.add(new Object[] {"Valid BSM", validBsm, true});

        final var bsmWithNullId = validProcessedBsm();
        bsmWithNullId.getFeatures()[0].getProperties().setId(null);
        bsms.add(new Object[] {"BSM with null id", bsmWithNullId, false});

        final var bsmWithNullSecMark = validProcessedBsm();
        bsmWithNullSecMark.getFeatures()[0].getProperties().setSecMark(null);
        bsms.add(new Object[] {"BSM with null secMark", bsmWithNullSecMark, false});

        final var bsmWithNullSpeed = validProcessedBsm();
        bsmWithNullSpeed.getFeatures()[0].getProperties().setSpeed(null);
        bsms.add(new Object[] {"BSM with null speed", bsmWithNullSpeed, false});

        final var bsmWithNullHeading = validProcessedBsm();
        bsmWithNullHeading.getFeatures()[0].getProperties().setHeading(null);
        bsms.add(new Object[] {"BSM with null heading", bsmWithNullHeading, false});

        final var bsmWithNullSource = validProcessedBsm();
        bsmWithNullSource.setOriginIp(null);
        bsmWithNullSource.setLogName(null);
        bsms.add(new Object[] {"BSM with null source", bsmWithNullSource, false});


        final var bsmWithNullRecordGeneratedAt = validProcessedBsm();
        bsmWithNullRecordGeneratedAt.setTimeStamp(null);
        bsms.add(new Object[] {"BSM with null recordGeneratedAt", bsmWithNullRecordGeneratedAt, false});

        final var bsmWithNullOdeReceivedAt = validProcessedBsm();
        bsmWithNullOdeReceivedAt.setOdeReceivedAt(null);
        bsms.add(new Object[] {"BSM with null odeReceivedAt", bsmWithNullOdeReceivedAt, false});


        return bsms;
    }



    
}
