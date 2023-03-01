package buyer;

import com.example.DistributedAssignment.services.*;
import common.GRPCClient;
import common.PurchaseHistoryPojo;
import common.SaleItemPojo;
import common.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static common.Utils.getSaleItemsForRestResponse;

public class ServerSideBuyersInterface implements BuyersInterface, GRPCClient {

    ShoppingCartItemServicesGrpc.ShoppingCartItemServicesBlockingStub shoppingCartItemServicesStub= ShoppingCartItemServicesGrpc.newBlockingStub(customerDBChannel);
    BuyerItemServicesGrpc.BuyerItemServicesBlockingStub buyerItemServicesStub = BuyerItemServicesGrpc.newBlockingStub(customerDBChannel);

    SaleItemServicesGrpc.SaleItemServicesBlockingStub saleItemServicesBlockingStub = SaleItemServicesGrpc.newBlockingStub(productDBChannel);

    @Override
    public String createAccount(String username, String password, String name) {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setName(name)
                .setUserType(UserType.BUYER)
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

    @Override
    public String logout(int buyerID) {
        return "{}";
    }

    @Override
    public String getSellerRating(int sellerID) {
        UserID userID = UserID.newBuilder().setUserId(sellerID).build();
        SellerRating value = getSellerRatingStub.getSellerRating(userID);
        return value.getSellerRating()+"";
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
    public List<SaleItemPojo> displayShoppingCart(int buyerID) {
        UserID userID = UserID.newBuilder().setUserId(buyerID).build();
        Iterator<SaleItem> values = shoppingCartItemServicesStub.displayShoppingCart(userID);
        return getSaleItemsForRestResponse(values);
    }

    @Override
    public String makePurchase(int userID) {
        List<SaleItemPojo> saleItems = displayShoppingCart(userID);
        clearShoppingCart(userID);
        for (SaleItemPojo saleItem: saleItems) {
            RemoveItemFromSaleRequest removeItemFromSaleRequest = RemoveItemFromSaleRequest
                    .newBuilder()
                    .setItemID(saleItem.getItemID())
                    .setSellerID(saleItem.getSellerID())
                    .setQuantity(saleItem.getQuantity())
                    .build();
            saleItemServicesBlockingStub.removeItemFromSale(removeItemFromSaleRequest);
            // TODO: Add Purchase History
        }

        return "{}";
    }

    @Override
    public List<PurchaseHistoryPojo> getBuyerPurchaseHistory(int buyerID) {
        UserID userID = UserID.newBuilder().setUserId(buyerID).build();
        Iterator<PurchaseHistoryResponse> values = buyerItemServicesStub.getBuyerPurchaseHistory(userID);
        List<PurchaseHistoryPojo> purchaseHistoryPojos = new ArrayList<>();
        while (values.hasNext()){
            PurchaseHistoryResponse value = values.next();
            PurchaseHistoryPojo purchaseHistoryPojo = new PurchaseHistoryPojo();
            purchaseHistoryPojo.setItemID(value.getItemID());
            purchaseHistoryPojo.setPurchaseID(value.getPurchaseID());
            purchaseHistoryPojo.setFeedback(value.getFeedback());
            purchaseHistoryPojo.setTimestamp(value.getTimestamp());
            purchaseHistoryPojo.setQuantity(value.getQuantity());
            purchaseHistoryPojos.add(purchaseHistoryPojo);
        }
        return purchaseHistoryPojos;
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
    public List<SaleItemPojo> searchItemsForSale(int category, String keywords) {
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .setCategory(category)
                .setKeywords(keywords)
                .build();
        Iterator<SaleItem> values = saleItemServicesBlockingStub.searchItemsForSale(searchRequest);
        return Utils.getSaleItemsForRestResponse(values);
    }
}
