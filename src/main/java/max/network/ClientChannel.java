package max.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ClientChannel extends AbsSocket {

    protected static final Logger LOG = LogManager.getLogger(ClientChannel.class);

    protected DatagramChannel channel;
    protected volatile SocketAddress addressServer;
    protected volatile boolean connected;
    protected volatile boolean requestSent;

    public ClientChannel() throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(null);
        addressServer = null;
    }

    /**
     * Функция для подключения к серверу по адресу
     * @param addressServer - адрес сервера
     */
    public void tryToConnect(InetSocketAddress addressServer) {
        this.addressServer = addressServer;
        sendCommand("connect");
        System.out.println("Trying to reach the server...");
        LOG.info("Trying to reach the server...");
    }

    /**
     * Функция для отправки байт-буфера
     * @param content - байт-буфер
     */
    public void sendDatagram(ByteBuffer content) throws IOException {
        channel.send(content, addressServer);
        this.requestSent = true;
        LOG.info("sent datagram to " + addressServer);
    }

    /**
     * Функция для получения датаграммы и записи ее в буфер
     * @param buffer - буфер, в который записывается датаграмма
     * @return ret - адрес сервера
     */
    public SocketAddress receiveDatagram(ByteBuffer buffer) throws IOException {
        return channel.receive(buffer);
    }

    /**
     * Функция для сериализации и отправки команды
     * @param command - отправляемая команда
     */
    public void sendCommand(Object command) {
        try(ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteArrayStream)) {

            objectStream.writeObject(command);
            LOG.info("send object " + command);
            final ByteBuffer objectBuffer = ByteBuffer.wrap(byteArrayStream.toByteArray());

            sendDatagram(objectBuffer);
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            LOG.error("", e);
        }
    }

    /**
     * Функция для отключения от сервера
     */
    public void disconnect() {
        try {
            channel.close();
        } catch (IOException e) {
            LOG.error("Error trying to disconnect, doing a forced out", e);
            System.exit(-1);
        }
    }

    /**
     * Функция для проверки подключения к серверу
     */
    public boolean isConnected() {
        return addressServer != null && connected;
    }

    /**
     * Функция для задания подключения/отключения к серверу
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Функция для задания отключения от сервера
     */
    public void setConnectionToFalse() {
        this.addressServer = null;
        this.connected = false;
    }

    /**
     * Функция получения информации о том, был ли отправлен запрос
     * @return boolean requestSent
     */
    public boolean requestWasSent() {
        return requestSent;
    }
    public void setRequestSent(boolean requestSent) {
        this.requestSent = requestSent;
    }
}