package seller;

import common.Client;
import common.SaleItemPojo;
import org.json.JSONObject;

import java.util.List;

import static common.Utils.SERVER_SIDE_SELLER_INTF_PORT;

public class ClientSideSellersInterface extends Client implements SellersInterface {

    public ClientSideSellersInterface(){
        address = "127.0.0.1";
        port = SERVER_SIDE_SELLER_INTF_PORT;
    }
    @Override
    public String createAccount(String username, String password, String sellerName) {
        JSONObject createAccountRequestJSON = new JSONObject();
        JSONObject argumentsJSON = new JSONObject();
        createAccountRequestJSON.put("function", "createAccount");
        argumentsJSON.put("username", username);
        argumentsJSON.put("password", password);
        argumentsJSON.put("sellerName", sellerName);
        createAccountRequestJSON.put("arguments", argumentsJSON);
        return sendRequest(createAccountRequestJSON.toString());
    }

    @Override
    public String login(String username, String password) {
        JSONObject loginRequestJSON = new JSONObject();
        loginRequestJSON.put("function", "login");
        JSONObject argumentsJSON = new JSONObject();
        argumentsJSON.put("username", username);
        argumentsJSON.put("password", password);
        loginRequestJSON.put("arguments", argumentsJSON);
        return sendRequest(loginRequestJSON.toString());
    }

    @Override
    public String logout(int sellerID) {
        return "";
    }

    @Override
    public String getSellerRating(int sellerID) {
        JSONObject argumentsJSON = new JSONObject();
        argumentsJSON.put("sellerID", sellerID);
        JSONObject getSellerRatingJSON = new JSONObject();
        getSellerRatingJSON.put("function", "getSellerRating");
        getSellerRatingJSON.put("arguments", argumentsJSON);
        return sendRequest(getSellerRatingJSON.toString());
    }

    @Override
    public String putItemForSale(SaleItemPojo item) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject putItemForSaleRequestJSON = new JSONObject();
        putItemForSaleRequestJSON.put("function", "putItemForSale");
        argumentsJSON.put("itemName",item.getItemName());
        argumentsJSON.put("category",item.getCategory());
        argumentsJSON.put("keywords",item.getKeyWords());
        argumentsJSON.put("isNew",item.getIsNew());
        argumentsJSON.put("itemPrice",item.getItemPrice());
        argumentsJSON.put("sellerID",item.getSellerID());
        argumentsJSON.put("quantity", item.getQuantity());
        putItemForSaleRequestJSON.put("arguments",argumentsJSON);
        return sendRequest(putItemForSaleRequestJSON.toString());
    }

    @Override
    public String changeSalePriceOfItem(int sellerID, int itemID, float newPrice) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject changeSalePriceOfItemJSON = new JSONObject();
        changeSalePriceOfItemJSON.put("function", "changeSalePriceOfItem");
        argumentsJSON.put("itemID",itemID);
        argumentsJSON.put("sellerID",sellerID);
        argumentsJSON.put("newPrice", newPrice);
        changeSalePriceOfItemJSON.put("arguments",argumentsJSON);
        return sendRequest(changeSalePriceOfItemJSON.toString());
    }

    @Override
    public String removeItemFromSale(int sellerID, int itemID, int quantity) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject removeItemFromSaleRequestJSON = new JSONObject();
        removeItemFromSaleRequestJSON.put("function", "removeItemFromSale");
        argumentsJSON.put("itemID",itemID);
        argumentsJSON.put("sellerID",sellerID);
        argumentsJSON.put("quantity", quantity);
        removeItemFromSaleRequestJSON.put("arguments",argumentsJSON);
        return sendRequest(removeItemFromSaleRequestJSON.toString());
    }

    @Override
    public List<SaleItemPojo> displayItemsOnSale(int sellerID) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject displayItemsOnSaleRequestJSON = new JSONObject();
        displayItemsOnSaleRequestJSON.put("function", "displayItemsOnSale");
        argumentsJSON.put("sellerID",sellerID);
        displayItemsOnSaleRequestJSON.put("arguments",argumentsJSON);
        String response =  sendRequest(displayItemsOnSaleRequestJSON.toString());
        return null;
    }


}
