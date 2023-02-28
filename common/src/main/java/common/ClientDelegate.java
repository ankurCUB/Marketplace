package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientDelegate {

    private final Socket socket;
    private final PrintWriter outputStream;

    private final BufferedReader inputStream;

    public ClientDelegate(String address, int port) throws IOException {
        socket = new Socket(address, port);
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new PrintWriter(socket.getOutputStream(), true);
    }

    public String sendRequest(String request) throws IOException {
        outputStream.println(request);
        String response = inputStream.readLine();
        outputStream.close();
        inputStream.close();
        return response;
    }

    public void disconnectClient() throws IOException {
        socket.close();
    }
}