package common;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.Server;
import java.io.IOException;
import java.net.InetSocketAddress;


public abstract class GRPCServer {


    private final Server server;

    public GRPCServer(int port) throws IOException {
        server = NettyServerBuilder.forAddress(new InetSocketAddress("localhost", 5004))
                .build();
    }

    public void startServer() throws IOException, InterruptedException {
        server.start();
        server.awaitTermination();
    }

    protected abstract String processClientRequest(String request);

    public void disconnectServer() throws IOException {

    }
}
