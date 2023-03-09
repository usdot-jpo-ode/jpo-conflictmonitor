package us.dot.its.jpo.ode.messagesender;

import lombok.Data;

@Data
public class HexLogItem {
    long timestamp;
    String dir;
    String hexMessage;
}
