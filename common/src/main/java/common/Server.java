package common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server {
    protected final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port,200000);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void startServer() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            ServerThread serverThread = new ServerThread(this, socket);
            serverThread.start();
        }
    }

    protected abstract String processClientRequest(String request);

    public void disconnectServer() throws IOException {
        serverSocket.close();
    }
}
