package customerDB;

import common.Utils;
import customerDB.services.BuyerService;
import customerDB.services.CredentialsService;
import customerDB.services.GetSellerRatingService;
import customerDB.services.ShoppingCartServices;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CustomerDBServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(Utils.CUSTOMER_DB_PORT)
                .addService(new BuyerService())
                .addService(new CredentialsService())
                .addService(new GetSellerRatingService())
                .addService(new ShoppingCartServices())
                .build();
        server.start();
        server.awaitTermination();
    }
}
