package seller;

import com.example.DistributedAssignment.services.*;
import common.GRPCClient;
import common.Server;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import static common.Utils.SERVER_SIDE_SELLER_INTF_PORT;
import static common.Utils.getResponseFromSaleItemsIterator;

public class ServerSideSellersInterface extends Server implements SellersInterface, GRPCClient {

    SaleItemServicesGrpc.SaleItemServicesBlockingStub saleItemServicesStub = SaleItemServicesGrpc.newBlockingStub(productDBChannel);

    public ServerSideSellersInterface(int port) throws IOException {
        super(port);
    }

    @Override
    public String createAccount(String username, String password, String name) {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setName(name)
                .setUserType(UserType.SELLER)
                .build();
        UserID value = credentialsStub.createAccount(createAccountRequest);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", value.getUserId());
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
        jsonObject.put("userID", value.getUserId());
        return jsonObject.toString();
    }

    public String logout(int sellerID) {
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
    public String putItemForSale(SaleItem item) {
        saleItemServicesStub.putItemForSale(item);
        return "{}";
    }

    @Override
    public String changeSalePriceOfItem(int sellerID, int itemID, float newPrice) {
        ChangeSalePriceRequest request = ChangeSalePriceRequest.newBuilder()
                .setSellerID(sellerID)
                .setItemID(itemID)
                .setNewPrice(newPrice)
                .build();
        saleItemServicesStub.changeSalePriceOfItem(request);
        return "{}";
    }

    @Override
    public String removeItemFromSale(int sellerID, int itemID, int quantity) {

        RemoveItemFromSaleRequest request = RemoveItemFromSaleRequest.newBuilder()
                .setSellerID(sellerID)
                .setItemID(itemID)
                .setQuantity(quantity)
                .build();

        saleItemServicesStub.removeItemFromSale(request);
        return "{}";
    }

    @Override
    public String displayItemsOnSale(int sellerID) {
        UserID userID = UserID.newBuilder()
                .setUserId(sellerID)
                .build();
        Iterator<SaleItem> values = saleItemServicesStub.displayItemsOnSale(userID);
        return getResponseFromSaleItemsIterator(values);
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
                String sellerName = arguments.getString("sellerName");
                response = createAccount(username, password, sellerName);
            } else if (invokedFunction.equalsIgnoreCase("login")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String username = arguments.getString("username");
                String password = arguments.getString("password");
                response = login(username, password);
            } else if (invokedFunction.equalsIgnoreCase("getSellerRating")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int userID = Integer.parseInt(arguments.getString("sellerID"));
                response = getSellerRating(userID);
            } else if (invokedFunction.equalsIgnoreCase("putItemForSale")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                SaleItem.Builder saleItemBuilder = com.example.DistributedAssignment.services.SaleItem.newBuilder();
                saleItemBuilder.setItemName(arguments.getString("itemName"));
                saleItemBuilder.setCategory(arguments.getInt("category"));
                saleItemBuilder.setKeyWords(arguments.getString("keywords"));
                saleItemBuilder.setIsNew(arguments.getInt("isNew"));
                saleItemBuilder.setItemPrice(arguments.getFloat("itemPrice"));
                saleItemBuilder.setSellerID(arguments.getInt("sellerID"));
                saleItemBuilder.setQuantity(arguments.getInt("quantity"));
                response = putItemForSale(saleItemBuilder.build());
            } else if (invokedFunction.equalsIgnoreCase("removeItemFromSale")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int itemID = arguments.getInt("itemID");
                int sellerID = arguments.getInt("sellerID");
                int quantity = arguments.getInt("quantity");
                response = removeItemFromSale(sellerID, itemID, quantity);
            } else if (invokedFunction.equalsIgnoreCase("changeSalePriceOfItem")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int sellerID = arguments.getInt("sellerID");
                int itemID = arguments.getInt("itemID");
                float newPrice = arguments.getFloat("newPrice");
                response = changeSalePriceOfItem(sellerID, itemID, newPrice);
            } else if (invokedFunction.equalsIgnoreCase("displayItemsOnSale")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int sellerID = arguments.getInt("sellerID");
                response = displayItemsOnSale(sellerID);
            }
        } catch (JSONException exception) {
            response = "{}";
        }
        return response;
    }

    public static void main(String[] args) {
        try {
            ServerSideSellersInterface serverSideSellersInterface = new ServerSideSellersInterface(SERVER_SIDE_SELLER_INTF_PORT);
            serverSideSellersInterface.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
