package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.ode.model.*;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils.validBsm;

/**
 * Unit tests for {@link BsmEventProcessor#validateBSM}
 */
@RunWith(Parameterized.class)
public class BsmEventProcessor_validateBSMTest {
    
    @Test
    public void testValidateBSM() {
        var processor = new BsmEventProcessor();
        var actual = processor.validateBSM(bsm);
        assertThat(description, actual, equalTo(expected));
    }

    private final String description;
    private final OdeBsmData bsm;
    private final boolean expected;

    public BsmEventProcessor_validateBSMTest(String description, OdeBsmData bsm, boolean expected) {
        this.description = description;
        this.bsm = bsm;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{index}: {0}, expected: {2}")
    public static Collection<Object[]> getParams() {

        var bsms = new ArrayList<Object[]>();

        bsms.add(new Object[] {"null BSM", null, false});

        final var emptyBsm = new OdeBsmData();
        bsms.add(new Object[] {"empty BSM", emptyBsm, false});

        final var emptyPayload = new OdeBsmPayload();
        final var emptyMetadata = new OdeBsmMetadata();

        final var bsmWithInvalidPayload = new OdeBsmData();
        bsmWithInvalidPayload.setPayload(new OdeMsgPayload());
        bsmWithInvalidPayload.setMetadata(emptyMetadata);
        bsms.add(new Object[] {"BSM with invalid payload class", bsmWithInvalidPayload, false});

        final var bsmWithInvalidMetadata = new OdeBsmData();
        bsmWithInvalidMetadata.setPayload(emptyPayload);
        bsmWithInvalidMetadata.setMetadata(new OdeMsgMetadata());
        bsms.add(new Object[] {"BSM with invalid metadata class", bsmWithInvalidMetadata, false});

        final var bsmWithInvalidData = new OdeBsmData();
        final var invalidPayload = new OdeBsmPayload();
        invalidPayload.setData(new OdeObject());
        bsmWithInvalidData.setPayload(invalidPayload);
        bsmWithInvalidData.setMetadata(emptyMetadata);
        bsms.add(new Object[] {"BSM with invalid data class", bsmWithInvalidData, false});

        final OdeBsmData validBsm = validBsm();
        bsms.add(new Object[] {"Valid BSM", validBsm, true});

        final var bsmWithNullId = validBsm();
        ((J2735Bsm)bsmWithNullId.getPayload().getData()).getCoreData().setId(null);
        bsms.add(new Object[] {"BSM with null id", bsmWithNullId, false});

        final var bsmWithNullSecMark = validBsm();
        ((J2735Bsm)bsmWithNullSecMark.getPayload().getData()).getCoreData().setSecMark(null);
        bsms.add(new Object[] {"BSM with null secMark", bsmWithNullSecMark, false});

        final var bsmWithNullSpeed = validBsm();
        ((J2735Bsm)bsmWithNullSpeed.getPayload().getData()).getCoreData().setSpeed(null);
        bsms.add(new Object[] {"BSM with null speed", bsmWithNullSpeed, false});

        final var bsmWithNullHeading = validBsm();
        ((J2735Bsm)bsmWithNullHeading.getPayload().getData()).getCoreData().setHeading(null);
        bsms.add(new Object[] {"BSM with null heading", bsmWithNullHeading, false});

        final var bsmWithNullSource = validBsm();
        ((OdeBsmMetadata)bsmWithNullSource.getMetadata()).setBsmSource(null);
        bsms.add(new Object[] {"BSM with null source", bsmWithNullSource, false});

        final var bsmWithNullOriginIp = validBsm();
        ((OdeBsmMetadata)bsmWithNullOriginIp.getMetadata()).setOriginIp(null);
        bsms.add(new Object[] {"BSM with null originIp", bsmWithNullOriginIp, false});

        final var bsmWithNullRecordGeneratedAt = validBsm();
        bsmWithNullRecordGeneratedAt.getMetadata().setRecordGeneratedAt(null);
        bsms.add(new Object[] {"BSM with null recordGeneratedAt", bsmWithNullRecordGeneratedAt, false});

        final var bsmWithNullOdeReceivedAt = validBsm();
        bsmWithNullOdeReceivedAt.getMetadata().setOdeReceivedAt(null);
        bsms.add(new Object[] {"BSM with null odeReceivedAt", bsmWithNullOdeReceivedAt, false});

        final var bsmWithNullLatitude = validBsm();
        ((J2735Bsm)bsmWithNullLatitude.getPayload().getData()).getCoreData().getPosition().setLatitude(null);
        bsms.add(new Object[] {"BSM with null latitude", bsmWithNullLatitude, false});

        final var bsmWithNullLongitude = validBsm();
        ((J2735Bsm)bsmWithNullLongitude.getPayload().getData()).getCoreData().getPosition().setLongitude(null);
        bsms.add(new Object[] {"BSM with null longitude", bsmWithNullLongitude, false});

        final var bsmWithNullMetadata = validBsm();
        bsmWithNullMetadata.setMetadata(null);
        bsms.add(new Object[] {"BSM with null metadata", bsmWithNullMetadata, false});

        final var bsmWithNullPayloadData = validBsm();
        bsmWithNullPayloadData.getPayload().setData(null);
        bsms.add(new Object[] {"BSM with null payload data", bsmWithNullPayloadData, false});

        final var bsmWithNullCoreData = validBsm();
        ((J2735Bsm)bsmWithNullCoreData.getPayload().getData()).setCoreData(null);
        bsms.add(new Object[] {"BSM with null core data", bsmWithNullCoreData, false});


        return bsms;
    }



    
}
