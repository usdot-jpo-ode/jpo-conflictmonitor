package us.dot.its.jpo.ode.messagesender;

import lombok.Data;

@Data
public class HexLogItem {
    long timeStamp;
    String dir;
    String hexMessage;
}
