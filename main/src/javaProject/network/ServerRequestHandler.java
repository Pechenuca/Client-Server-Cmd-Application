package javaProject.network;

import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.exception.OrgFormatException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import java.nio.ByteBuffer;

public class ServerRequestHandler {

    protected static final Logger LOG = LogManager.getLogger(ServerRequestHandler.class);

    private final ServerSocket socket;
    private final ExecutionContext executionContext;
    private SocketAddress addressFromClient;

    public ServerRequestHandler(ServerSocket socket, ExecutionContext context) {
        this.socket = socket;
        this.executionContext = context;
        this.addressFromClient = null;
    }

    public void receiveFromWherever() {
        try {
            receiveData();
        } catch (SocketTimeoutException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Weird errors, check log");
            LOG.error("Weird errors processing the received data", e);
            socket.sendResponse("Weird errors, check log. " + e.getMessage(), addressFromClient);
        }
    }

    /**
     * Функция для получения данных
     */
    public void receiveData() throws IOException, ClassNotFoundException {
        final ByteBuffer buf = ByteBuffer.allocate(AbsSocket.DATA_SIZE);
        SocketAddress addressFromClient = socket.receiveDatagram(buf);
        buf.flip();
        final byte[] petitionBytes = new byte[buf.remaining()];
        buf.get(petitionBytes);

        this.addressFromClient = addressFromClient;
        socket.checkClient(addressFromClient);
        if (petitionBytes.length > 0)
            processRequest(petitionBytes);
    }
    /**
     * Функция для десериализации данных
     * @param petitionBytes - полученные данные
     */
    private void processRequest(byte[] petitionBytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(petitionBytes))) {
            final Object obj = stream.readObject();
            LOG.info("received object: " + obj);
            if (obj == null)
                throw new ClassNotFoundException();
            executeObj(obj);
        }
    }
    /**
     * Функция для работы с командами клиента
     * @param obj - полученная от клиента команда
     */
    private void executeObj(Object obj) throws IOException {
        Object responseExecution;
        if (obj instanceof String)
            responseExecution = obj;
        else {
            Command command = (Command) obj;
            try {
                responseExecution = command.execute(executionContext);
            }catch (OrgFormatException ex) {
                responseExecution = ex.getMessage();
                LOG.error(ex.getMessage(), ex);
            } catch (NumberFormatException ex) {
                responseExecution = "Incorrect format of the entered value";
                LOG.error("Incorrect format of the entered value", ex);
            } catch (ArrayIndexOutOfBoundsException ex) {
                responseExecution = "There is a problem in the amount of args passed";
                LOG.error("There is a problem in the amount of args passed", ex);
            } catch (SecurityException ex) {
                responseExecution = "Security problems trying to access to the file (Can not be read or edited)";
                LOG.error("Security problems trying to access to the file (Can not be read or edited)", ex);
            }
        }
        socket.sendResponse(responseExecution, addressFromClient);
    }
    /**
     * Функция для отключения сервера
     */
    public void disconnect() {
        LOG.info("Disconnecting the server...");
        System.out.println("Disconnecting the server...");
        socket.getSocket().disconnect();
    }
}