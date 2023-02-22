package common;

import java.io.IOException;

public abstract class Client {

    protected String address;
    protected int port;
    protected String sendRequest(String request) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate(address, port);
            return clientDelegate.sendRequest(request);
        } catch (IOException exception) {
            exception.printStackTrace();
            return "{}";
        }
    }
}
