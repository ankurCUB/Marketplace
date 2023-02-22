package buyer;

import com.example.DistributedAssignment.services.*;
import common.GRPCClient;
import common.Server;
import common.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import static common.Utils.SERVER_SIDE_BUYER_INTF_PORT;
import static common.Utils.getResponseFromSaleItemsIterator;

public class ServerSideBuyersInterface extends Server implements BuyersInterface, GRPCClient {

    ShoppingCartItemServicesGrpc.ShoppingCartItemServicesBlockingStub shoppingCartItemServicesStub= ShoppingCartItemServicesGrpc.newBlockingStub(customerDBChannel);
    BuyerItemServicesGrpc.BuyerItemServicesBlockingStub buyerItemServicesStub = BuyerItemServicesGrpc.newBlockingStub(customerDBChannel);
    public ServerSideBuyersInterface(int port) throws IOException {
        super(port);
    }

    @Override
    public String createAccount(String username, String password, String name) {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setName(name)
                .setUserType(UserType.BUYER)
                .build();
        UserID value = credentialsStub.createAccount(createAccountRequest);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID",value.getUserId());
        return jsonObject.toString();
    }

    @Override
    public String login(String username, String password) {
        LoginRequest loginRequest = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        UserID value = credentialsStub.login(loginRequest);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID",value.getUserId());
        return jsonObject.toString();
    }

    @Override
    public String logout(int buyerID) {
        return "{}";
    }

    @Override
    public String getSellerRating(int sellerID) {
        UserID userID = UserID.newBuilder().setUserId(sellerID).build();
        SellerRating value = getSellerRatingStub.getSellerRating(userID);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sellerRating", value.getSellerRating());
        return jsonObject.toString();
    }

    @Override
    public String addItemToShoppingCart(int userID, int itemID, int quantity) {

        ItemToCartRequest itemToCartRequest = ItemToCartRequest.newBuilder()
                .setUserID(userID)
                .setItemID(itemID)
                .setQuantity(quantity)
                .build();
        shoppingCartItemServicesStub.addItemToShoppingCart(itemToCartRequest);
        return "{}";
    }

    @Override
    public String removeItemFromShoppingCart(int userID, int itemID, int quantity) {
        ItemToCartRequest itemToCartRequest = ItemToCartRequest.newBuilder()
                .setItemID(itemID)
                .setUserID(userID)
                .setQuantity(quantity)
                .build();
        shoppingCartItemServicesStub.removeItemFromShoppingCart(itemToCartRequest);
        return "{}";
    }

    @Override
    public String clearShoppingCart(int buyerID) {
        UserID userID = UserID.newBuilder()
                .setUserId(buyerID)
                .build();
        shoppingCartItemServicesStub.clearShoppingCart(userID);
        return "{}";
    }

    @Override
    public String displayShoppingCart(int buyerID) {
        UserID userID = UserID.newBuilder().setUserId(buyerID).build();
        Iterator<SaleItem> values = shoppingCartItemServicesStub.displayShoppingCart(userID);
        return getResponseFromSaleItemsIterator(values);
    }

    @Override
    public String makePurchase() {
        return null;
    }

    @Override
    public String getBuyerPurchaseHistory(int buyerID) {
        UserID userID = UserID.newBuilder().setUserId(buyerID).build();
        Iterator<PurchaseHistoryResponse> values = buyerItemServicesStub.getBuyerPurchaseHistory(userID);
        final JSONArray jsonArray = new JSONArray();
        while (values.hasNext()){
            PurchaseHistoryResponse value = values.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemID", value.getItemID());
            jsonObject.put("quantity", value.getQuantity());
            jsonObject.put("timestamp", value.getTimestamp());
            jsonObject.put("purchaseID", value.getPurchaseID());
            jsonObject.put("feedback", value.getFeedback());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    @Override
    public String provideFeedback(int purchaseID, int feedback) {
        FeedbackRequest feedbackRequest = FeedbackRequest.newBuilder()
                .setPurchaseID(purchaseID)
                .setLikeOrDislike(feedback)
                .build();
        buyerItemServicesStub.provideFeedback(feedbackRequest);
        return "{}";
    }

    @Override
    public String searchItemsForSale(int category, String keywords) {
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .setCategory(category)
                .setKeywords(keywords)
                .build();
        Iterator<SaleItem> values = buyerItemServicesStub.searchItemsForSale(searchRequest);
        return Utils.getResponseFromSaleItemsIterator(values);
    }


    @Override
    protected String processClientRequest(String request) {
        JSONObject jsonObject = new JSONObject(request);
        String response = "{} ";
        try {
            String invokedFunction = jsonObject.getString("function");
            if (invokedFunction.equalsIgnoreCase("createAccount")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String username = arguments.getString("username");
                String password = arguments.getString("password");
                String buyerName = arguments.getString("buyerName");
                createAccount(username, password, buyerName);
            } else if (invokedFunction.equalsIgnoreCase("login")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String username = arguments.getString("username");
                String password = arguments.getString("password");
                login(username, password);
            } else if (invokedFunction.equalsIgnoreCase("addItemToShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = arguments.getInt("buyerID");
                int itemID = arguments.getInt("itemID");
                int quantity = arguments.getInt("quantity");
                response = addItemToShoppingCart(buyerID, itemID, quantity);
            } else if (invokedFunction.equalsIgnoreCase("removeItemFromShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = arguments.getInt("buyerID");
                int itemID = arguments.getInt("itemID");
                int quantity = arguments.getInt("quantity");
                response = removeItemFromShoppingCart(buyerID, itemID, quantity);
            } else if (invokedFunction.equalsIgnoreCase("clearShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = arguments.getInt("buyerID");
                response = clearShoppingCart(buyerID);
            } else if (invokedFunction.equalsIgnoreCase("getSellerRating")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int sellerID = Integer.parseInt(arguments.getString("sellerID"));
                response = getSellerRating(sellerID);
            } else if (invokedFunction.equalsIgnoreCase("displayShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = Integer.parseInt(arguments.getString("buyerID"));
                response = displayShoppingCart(buyerID);
            } else if (invokedFunction.equalsIgnoreCase("getBuyerPurchaseHistory")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = Integer.parseInt(arguments.getString("buyerID"));
                response =  getBuyerPurchaseHistory(buyerID);
            } else if (invokedFunction.equalsIgnoreCase("provideFeedback")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int purchaseID = Integer.parseInt(arguments.getString("purchaseID"));
                int feedback = Integer.parseInt(arguments.getString("feedback"));
                response = provideFeedback(purchaseID, feedback);
            } else if (invokedFunction.equalsIgnoreCase("searchItemsForSale")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int category = Integer.parseInt(arguments.getString("purchaseID"));
                String keywords = arguments.getString("keywords");
                response = searchItemsForSale(category, keywords);
            }
        } catch (JSONException exception) {
            response = "{}";
        }
        return response;
    }

    public static void main(String[] args) {

        try {
            ServerSideBuyersInterface serverSideBuyersInterface =
                    new ServerSideBuyersInterface(SERVER_SIDE_BUYER_INTF_PORT);
            serverSideBuyersInterface.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
