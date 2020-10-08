package max.network;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;


public abstract class AbsSocket {

    protected static final Logger LOG = LogManager.getLogger(AbsSocket.class);

    public static final int DATA_SIZE = 16000;
    public static final int SOCKET_TIMEOUT = 3000;

    /*protected DatagramSocket socket = null;
    protected SocketAddress addressTo = null;
    protected boolean connected = false;
    protected Thread receiverThread = null;*/

    public AbsSocket() throws SocketException {

    }

    public abstract void sendDatagram(ByteBuffer content) throws IOException;
    public abstract SocketAddress receiveDatagram(ByteBuffer content) throws IOException;


}