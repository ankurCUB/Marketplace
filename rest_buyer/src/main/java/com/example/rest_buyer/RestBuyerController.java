package com.example.rest_buyer;

import buyer.ServerSideBuyersInterface;
import com.example.DistributedAssignment.services.UserID;
import com.example.rest_buyer.RPCExceptions.SellerNotFoundError;
import com.example.rest_buyer.dataModel.*;
import common.PurchaseHistoryPojo;
import common.SaleItemPojo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestBuyerController {
    private final ServerSideBuyersInterface serverSideBuyersInterface;

    RestBuyerController() {
        serverSideBuyersInterface = new ServerSideBuyersInterface();
    }

    @PostMapping("/buyers")
    UserIDDataModel newSellerAccount(@RequestBody CreateAccountDataModel createAccountDataModel) {
        int userID = Integer.parseInt(serverSideBuyersInterface.createAccount(
                createAccountDataModel.getUsername(),
                createAccountDataModel.getPassword(),
                createAccountDataModel.getName()));
        return new UserIDDataModel(userID);
    }

    @GetMapping("/buyers/login")
    UserIDDataModel login(@RequestBody LoginDataModel loginDataModel) {
        int userID = Integer.parseInt(serverSideBuyersInterface.login(
                loginDataModel.getUsername(),
                loginDataModel.getPassword()
        ));
        return new UserIDDataModel(userID);
    }

    @PostMapping("/buyers/logout")
    void logout(@RequestBody UserID userID) {
    }

    @GetMapping("/rating/{sellerID}")
    SellerRatingDataModel sellerRating(@PathVariable Long sellerID) {
        float rating = Float.parseFloat(serverSideBuyersInterface.getSellerRating(sellerID.intValue()));
        if (rating == -1) {
            throw new SellerNotFoundError(sellerID);
        } else {
            return new SellerRatingDataModel(sellerID, rating);
        }
    }

    @PostMapping("/shoppingcart")
    void addItemToShoppingCart(@RequestBody ShoppingCartDataModel shoppingCartDataModel) {
        serverSideBuyersInterface.addItemToShoppingCart(shoppingCartDataModel.getUserID(),
                shoppingCartDataModel.getItemID(),
                shoppingCartDataModel.getQuantity());
    }

    @DeleteMapping("/shoppingcart")
    void deleteItemFromShoppingCart(@RequestBody ShoppingCartDataModel shoppingCartDataModel) {
        serverSideBuyersInterface.removeItemFromShoppingCart(shoppingCartDataModel.getUserID(),
                shoppingCartDataModel.getItemID(), shoppingCartDataModel.getQuantity());
    }

    @DeleteMapping("/shoppingcart/{userID}")
    void clearShoppingCart(@PathVariable int userID) {
        serverSideBuyersInterface.clearShoppingCart(userID);
    }

    @GetMapping("/shoppingcart/{userID}")
    List<SaleItemPojo> displayShoppingCart(@PathVariable Long userID){
        return serverSideBuyersInterface.displayShoppingCart(userID.intValue());
    }

    @GetMapping("/items/search")
    List<SaleItemPojo> searchForItemsOnSale(@RequestBody SearchItemDataModel searchItemDataModel){
        return serverSideBuyersInterface.searchItemsForSale(searchItemDataModel.getCategory(), searchItemDataModel.getKeywords());
    }

    @GetMapping("/buyers/purchase_history/{userID}")
    List<PurchaseHistoryPojo> getPurchaseHistory(@PathVariable int userID){
        return serverSideBuyersInterface.getBuyerPurchaseHistory(userID);
    }

    @PostMapping("/buyers/feedback/{purchaseID}/{feedback}")
    void provideFeedback(@PathVariable int feedback, @PathVariable int purchaseID){
        serverSideBuyersInterface.provideFeedback(purchaseID, feedback);
    }

}
