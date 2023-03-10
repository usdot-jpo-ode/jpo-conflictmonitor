package us.dot.its.jpo.ode.messagesender.scriptrunner.hex;

import lombok.Getter;


/**
 * Includes J2735 Message Frame IDs found in the Utah test data.
 */
public enum DSRCmsgID {
    MAP(18, "0012", 44920, "topic.OdeMapJson"), 
    SPAT(19, "0013", 44910, "topic.OdeSpatJson"), 
    BSM(20, "0014", 46800, "topic.OdeBsmJson"), 
    RTCM(28, "001C", 0, ""),
    TEST_MESSAGE_01(241, "00F1", 0, "")
    ;

    @Getter private int msgId;
    @Getter private String hex;
    @Getter int udpPort;
    @Getter String topic;

    private DSRCmsgID(int msgId, String hex, int udpPort, String topic) {
        this.msgId = msgId;
        this.hex = hex;
        this.udpPort = udpPort;
        this.topic = topic;
    }

    public static DSRCmsgID fromMsgId(int msgId) {
        for (DSRCmsgID dsrcMsgId : DSRCmsgID.values()) {
            if (dsrcMsgId.getMsgId() == msgId) {
                return dsrcMsgId;
            }
        }
        return null;
    }

    public static DSRCmsgID fromHex(String hex) {
        for (DSRCmsgID dsrcMsgId : DSRCmsgID.values()) {
            if (dsrcMsgId.getHex().equals(hex)) {
                return dsrcMsgId;
            }
        }
        return null;
    }

    public static DSRCmsgID fromTopic(String topic) {
        for (DSRCmsgID dsrcMsgId : DSRCmsgID.values()) {
            if (dsrcMsgId.getTopic().equals(topic)) {
                return dsrcMsgId;
            }
        }
        return null;
    }
}
