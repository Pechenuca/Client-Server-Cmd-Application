package max.network;

import max.database.Credentials;
import max.database.CurrentUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.List;

public class ClientResponseHandler {

    private class ResponseReceiver extends Thread {

        protected volatile Object receivedObject = null;

        @Override
        public void run() {
            while (true) {
                try {
                    receiveData();
                } catch (ClosedChannelException ignored) {
                } catch (EOFException ex) {
                    System.err.println("Reached limit of data to receive");
                    LOG.error("Reached Limit", ex);
                } catch (IOException | ClassNotFoundException e) {
                    LOG.error("I/O Problems", e);
                }
            }
        }

        /**
         * Функция для получения данных
         */
        public void receiveData() throws IOException, ClassNotFoundException {
            final ByteBuffer buf = ByteBuffer.allocate(AbsSocket.DATA_SIZE);
            final SocketAddress addressFromServer = channel.receiveDatagram(buf);
            buf.flip();

            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);

            if (bytes.length < 1)
                return;

            synchronized (ClientResponseHandler.class) {
                channel.setRequestSent(false);
                if (bytes.length < AbsSocket.DATA_SIZE)
                    receivedObject = processResponse(bytes);
                else
                    throw new EOFException();
            }
        }

        /**
         * Функция для десериализации полученных данных
         * @param petitionBytes - данные
         * @return obj - объект десериализованных данных
         */
        private Object processResponse(byte[] petitionBytes) throws IOException, ClassNotFoundException {
            try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(petitionBytes))) {
                final Object obj = stream.readObject();
                LOG.info("received object: " + obj);
                if (obj == null)
                    throw new ClassNotFoundException();
                return obj;
            }
        }
    }


    protected static final Logger LOG = LogManager.getLogger(ClientResponseHandler.class);
    private final ResponseReceiver receiverThread;
    private final ClientChannel channel;
    private final CurrentUser currentUser;

    public ClientResponseHandler(ClientChannel channel, CurrentUser currentUser) {
        this.channel = channel;
        this.currentUser = currentUser;
        LOG.info("starting receiver");
        receiverThread = new ResponseReceiver();
        receiverThread.setName("ClientReceiverThread");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public Object checkForResponse() {
        Object received = receiverThread.receivedObject;

        final long start = System.currentTimeMillis();
        while (channel.requestWasSent()) {
            if (channel.requestWasSent() && System.currentTimeMillis() - start > 1500) {
                LOG.error("Seems the server went down!");
                channel.setConnectionToFalse();
                return "The server is down, please check the connection";
            }
        }

        if (received != null) {
            channel.setConnected(true);
            /* In the case a weird user was put cheating, we logout */
            if (received instanceof Credentials && ((Credentials)received).id == -1) {
                LOG.error("Seems you were doing something nasty, go out of my system");
                System.exit(0);
            }
        }

        return received;
    }

    public void setReceivedObjectToNull() {
        synchronized (this) {
            receiverThread.receivedObject = null;
        }
    }

    public void setCurrentUser(Credentials credentials) {
        currentUser.setCredentials(credentials);
    }
    public CurrentUser getCurrentUser() {
        return currentUser;
    }


    public void finishReceiver() {
        receiverThread.interrupt();
    }


    public ResponseReceiver getReceiver() {
        return receiverThread;
    }
}