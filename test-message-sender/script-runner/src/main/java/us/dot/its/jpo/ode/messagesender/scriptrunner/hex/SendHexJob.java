package us.dot.its.jpo.ode.messagesender.scriptrunner.hex;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendHexJob implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(SendHexJob.class);

    private DSRCmsgID msgId;
    private long sendTime;
    private String hexMessage;
    private String dockerHostIp;
    
    @Override
    public void run() {
        // Send hex message to UDP port
        try (DatagramSocket socket = new DatagramSocket()) {
            final int udpPort = msgId.getUdpPort();
            InetAddress address = InetAddress.getByName(dockerHostIp);
            byte[] buf = Hex.decodeHex(hexMessage);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, udpPort);
            socket.send(packet);
            logger.info("{}: Sent {} to udp {}:{}", sendTime, msgId, dockerHostIp, udpPort);
        } catch (IOException | DecoderException e) {
            logger.error("Error sending hex message to UDP port", e);
        }
    }
 
}
