package seller;

import com.example.DistributedAssignment.services.*;
import common.GRPCClient;
import common.SaleItemPojo;

import java.util.Iterator;
import java.util.List;

import static common.Utils.getSaleItemsForRestResponse;

public class ServerSideSellersInterface implements SellersInterface, GRPCClient {

    SaleItemServicesGrpc.SaleItemServicesBlockingStub saleItemServicesStub = SaleItemServicesGrpc.newBlockingStub(productDBChannel);

    @Override
    public String createAccount(String username, String password, String name) {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setName(name)
                .setUserType(UserType.SELLER)
                .build();
        UserID value = credentialsStub.createAccount(createAccountRequest);
        return value.getUserId() + "";
    }

    @Override
    public String login(String username, String password) {
        LoginRequest loginRequest = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        UserID value = credentialsStub.login(loginRequest);
        return value.getUserId() + "";
    }

    public String logout(int sellerID) {
        return "{}";
    }

    @Override
    public String getSellerRating(int sellerID) {
        UserID userID = UserID.newBuilder().setUserId(sellerID).build();
        SellerRating value = getSellerRatingStub.getSellerRating(userID);
        return value.getSellerRating()+"";
    }

    @Override
    public String putItemForSale(SaleItemPojo item) {
        SaleItem saleItem = SaleItem.newBuilder()
                .setItemID(item.getItemID())
                .setItemName(item.getItemName())
                .setItemPrice(item.getItemPrice())
                .setKeyWords(item.getKeyWords())
                .setIsNew(item.getIsNew())
                .setQuantity(item.getQuantity())
                .setSellerID(item.getSellerID())
                .build();
        saleItemServicesStub.putItemForSale(saleItem);
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
    public List<SaleItemPojo> displayItemsOnSale(int sellerID) {
        UserID userID = UserID.newBuilder()
                .setUserId(sellerID)
                .build();
        Iterator<SaleItem> values = saleItemServicesStub.displayItemsOnSale(userID);
        return getSaleItemsForRestResponse(values);
    }
}
