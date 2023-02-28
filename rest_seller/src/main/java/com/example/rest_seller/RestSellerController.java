package com.example.rest_seller;

import com.example.DistributedAssignment.services.UserID;
import com.example.rest_seller.RPCExceptions.SellerNotFoundError;
import com.example.rest_seller.dataModel.*;

import common.SaleItemPojo;

import org.springframework.web.bind.annotation.*;
import seller.ServerSideSellersInterface;

import java.util.List;

@RestController
public class RestSellerController {
    private final ServerSideSellersInterface serverSideSellersInterface;

    RestSellerController() {
        serverSideSellersInterface = new ServerSideSellersInterface();
    }

    @PostMapping("/sellers")
    UserIDDataModel newSellerAccount(@RequestBody CreateAccountDataModel createAccountDataModel) {
        int userID = Integer.parseInt(serverSideSellersInterface.createAccount(
                createAccountDataModel.getUsername(),
                createAccountDataModel.getPassword(),
                createAccountDataModel.getName()));
        return new UserIDDataModel(userID);
    }

    @GetMapping("/sellers/login")
    UserIDDataModel login(@RequestBody LoginDataModel loginDataModel) {
        int userID = Integer.parseInt(serverSideSellersInterface.login(
                loginDataModel.getUsername(),
                loginDataModel.getPassword()
        ));
        return new UserIDDataModel(userID);
    }

    @PostMapping("/sellers/logout")
    void logout(@RequestBody UserID userID) {
    }

    @GetMapping("/rating/{sellerID}")
    SellerRatingDataModel sellerRating(@PathVariable Long sellerID) {
        float rating = Float.parseFloat(serverSideSellersInterface.getSellerRating(sellerID.intValue()));
        if(rating == -1){
            throw new SellerNotFoundError(sellerID);
        } else {
            return new SellerRatingDataModel(sellerID, rating);
        }
    }

    @PostMapping("/items")
    void putItemForSale(@RequestBody SaleItemDataModel saleItemDataModel) {
        SaleItemPojo saleItem = new SaleItemPojo();
        saleItem.setSellerID(saleItemDataModel.getSellerID());
        saleItem.setItemID(saleItemDataModel.getItemID());
        saleItem.setItemName(saleItemDataModel.getItemName());
        saleItem.setItemPrice(saleItemDataModel.getItemPrice());
        saleItem.setQuantity(saleItemDataModel.getQuantity());
        saleItem.setIsNew(saleItemDataModel.getIsNew());saleItem.setKeyWords(saleItemDataModel.getKeyWords());
        serverSideSellersInterface.putItemForSale(saleItem);
    }

    @PutMapping("/items")
    void changeSalePriceOfItem(@RequestBody NewPriceDataModel newPriceDataModel) {
        serverSideSellersInterface.changeSalePriceOfItem(newPriceDataModel.getSellerID()
                , newPriceDataModel.getItemID()
                , newPriceDataModel.getNewPrice());
    }

    @DeleteMapping("/items/{sellerId}/{itemId}/{quantity}")
    void removeItemFromSale(@PathVariable int sellerId, @PathVariable int itemId, @PathVariable int quantity)
    {
        serverSideSellersInterface.removeItemFromSale(sellerId, itemId, quantity);
    }

    @GetMapping("/items/{sellerID}")
    List<SaleItemPojo> getItemsOnSale(@PathVariable Long sellerID){
        return serverSideSellersInterface.displayItemsOnSale(sellerID.intValue());

    }

}
