import buyer.BuyersInterface;
import buyer.ClientSideBuyersInterface;
import org.json.JSONObject;
import seller.ClientSideSellersInterface;
import seller.SellersInterface;

import java.util.ArrayList;
import java.util.List;

public class MarketPlace {
    public static void main(String[] args) {
//        SellersInterface clientSideSellersInterface = new ClientSideSellersInterface();
//        String response = clientSideSellersInterface.login("Seller" + 999, "password");
//        System.out.println(response);

        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            Thread sellerThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    SellersInterface clientSideSellersInterface = new ClientSideSellersInterface();


//                    JSONObject userIDJSON;
//                    int sellerID;

//                    response = clientSideSellersInterface.createAccount("Seller" + finalI, "password", "sellerName");
//                    userIDJSON = new JSONObject(response);
//                    sellerID = Integer.parseInt(userIDJSON.getString("userID"));
//
                    long start = System.currentTimeMillis();
                    for(int i=0; i<1000;i++) {
                        clientSideSellersInterface.login("Seller" + finalI, "password");
                    }
                    long end = System.currentTimeMillis();
                    times.add(end-start);
                    System.out.println(times);



//                    JSONArray array = new JSONArray(response);
//                    userIDJSON = array.getJSONObject(0);
//                    sellerID = Integer.parseInt(userIDJSON.getString("userID"));
//
//                    response = clientSideSellersInterface.getSellerRating(sellerID);
//
//                    response = clientSideSellersInterface.putItemForSale(new SaleItem("itemName", 0, "kw1, kw2, kw3", 1, 9.5f, sellerID, finalI % 20));
//
//                    response = clientSideSellersInterface.removeItemFromSale(sellerID, finalI % 10, finalI % 20 - 2);
//
//                    response = clientSideSellersInterface.displayItemsOnSale(sellerID);

                }
            };
            sellerThread.start();

            Thread buyerThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    BuyersInterface clientSideBuyersInterface = new ClientSideBuyersInterface();
                    int userID;
                    JSONObject userIDJSON;

//                    response = clientSideBuyersInterface.createAccount("Buyer" + finalI, "password", "buyerName");
//                    userIDJSON = new JSONObject(response);
//                    userID = Integer.parseInt(userIDJSON.getString("userID"));
//
                    long start = System.currentTimeMillis();
                    for(int i=0; i<1000;i++) {
                        clientSideBuyersInterface.login("Buyer" + finalI, "password");
                    }
                    long end = System.currentTimeMillis();
                    times.add(end-start);
                    System.out.println(times);


//                    userIDJSON = new JSONObject(response);
//                    userID = Integer.parseInt(userIDJSON.getString("userID"));
//                    response = clientSideBuyersInterface.addItemToShoppingCart(userID, userID, 5);
//
//                    response = clientSideBuyersInterface.getSellerRating(userID);
//
//                    response = clientSideBuyersInterface.addItemToShoppingCart(userID, userID, 5);
//
//                    response = clientSideBuyersInterface.removeItemFromShoppingCart(userID, userID, 6);
//
//                    response = clientSideBuyersInterface.clearShoppingCart(userID);

                }
            };
            buyerThread.start();

        }
    }

}
