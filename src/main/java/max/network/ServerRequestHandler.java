package max.network;

import max.command.Command;
import max.command.ExecutionContext;
import max.database.Credentials;
import max.exception.OrgFormatException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import java.nio.ByteBuffer;
import java.util.concurrent.*;

public class ServerRequestHandler {

    private class RequestReceiver extends Thread {

        @Override
        public void run() {
            while (true) {
                receiveData();
            }
        }

        /**
         * Функция для получения данных
         */
        public void receiveData() {
            SocketAddress addressFromClient = null;
            try {
                final ByteBuffer buf = ByteBuffer.allocate(AbsSocket.DATA_SIZE);
                addressFromClient = socket.receiveDatagram(buf);
                buf.flip();
                final byte[] petitionBytes = new byte[buf.remaining()];
                buf.get(petitionBytes);

                if (petitionBytes.length > 0)
                    processRequest(petitionBytes, addressFromClient);

            } catch (SocketTimeoutException ignored) {
            } catch (IOException | ClassNotFoundException e) {
                LOG.error("Weird errors processing the received data", e);
                executeObj("Weird errors, check log. " + e.getMessage(), addressFromClient);
            }
        }

        /**
         * Функция для десериализации данных
         * @param petitionBytes - полученные данные
         */
        private void processRequest(byte[] petitionBytes, SocketAddress addressFromClient) throws IOException, ClassNotFoundException {
            try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(petitionBytes))) {
                final Object obj = stream.readObject();
                LOG.info("received object: " + obj);
                if (obj == null)
                    throw new ClassNotFoundException();
                executeObj(obj, addressFromClient);
            }
        }
    }

    protected static final Logger LOG = LogManager.getLogger(ServerRequestHandler.class);

    private final ServerSocket socket;
    private final ExecutionContext executionContext;
    private final RequestReceiver requestReceiver;
    private final ExecutorService executor;

    public ServerRequestHandler(ServerSocket socket, ExecutionContext context) {
        this.socket = socket;
        this.executionContext = context;
        requestReceiver = new RequestReceiver();
        requestReceiver.setName("ServerReceiverThread");
        executor = Executors.newCachedThreadPool();
    }

    public void receiveFromWherever() {
        requestReceiver.start();
    }


    /**
     * Функция для работы с командами клиента
     * @param obj - полученная от клиента команда
     * @param addressFromClient - address to send back the response
     */
    private void executeObj(Object obj, SocketAddress addressFromClient) {
        Future<Object> resulted = executor.submit(() -> {
            Object responseExecution;
            if (obj instanceof String)
                responseExecution = obj;
            else {
                Command command = ((CommandPacket) obj).getCommand();
                Credentials credentials = ((CommandPacket) obj).getCredentials();
                executionContext.setResourcesBundle(((CommandPacket) obj).getLocale());
                try {
                    responseExecution = command.execute(executionContext, credentials);
                }catch (OrgFormatException ex) {
                    responseExecution = ex.getMessage();
                    LOG.error(ex.getMessage(), ex);
                } catch (NumberFormatException ex) {
                    responseExecution = executionContext.resourcesBundle().getString("server.response.error.format.arguments");
                    LOG.error("Incorrect format of the entered value", ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    responseExecution = executionContext.resourcesBundle().getString("server.response.error.amount.arguments");
                    LOG.error("There is a problem in the amount of args passed", ex);
                } catch (SecurityException ex) {
                    responseExecution = executionContext.resourcesBundle().getString("server.response.error.access.security");
                    LOG.error("Security problems trying to access to the file (Can not be read or edited)", ex);
                } catch (IOException ex) {
                    responseExecution = ex.getMessage();
                    LOG.error("I/O problem: ", ex);
                }
            }
            socket.sendResponse(responseExecution, addressFromClient);
            return responseExecution;
        });

        try {
            System.out.println("Future Object gotten from executor: \n" + resulted.get().toString());
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error getting result from executor", e);
        }
    }


    /**
     * Функция для отключения сервера
     */
    public void disconnect() {
        LOG.info("Disconnecting the server...");
        try {
            executor.shutdown();
            executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.error("Interrupted executor during shutdown",e);
        }
        socket.disconnect();
        requestReceiver.interrupt();
    }
}