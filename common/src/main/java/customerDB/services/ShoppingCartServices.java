package customerDB.services;

import com.example.DistributedAssignment.services.*;
import com.example.DistributedAssignment.services.Void;
import common.Utils;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoppingCartServices extends ShoppingCartItemServicesGrpc.ShoppingCartItemServicesImplBase {
    @Override
    public void addItemToShoppingCart(ItemToCartRequest request, StreamObserver<Void> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToCustomerDB();
            String query = "SELECT quantity from ShoppingCart where \"userID\" = " + request.getUserID()
                    + " and \"itemID\" = \"" + request.getItemID() + "\"";
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            if (resultSet.next()) {
                int currentCartValueForItem = resultSet.getInt(1);
                query = "UPDATE ShoppingCart SET \"quantity\" = " + (currentCartValueForItem + request.getQuantity())
                        + " WHERE \"userID\" = " + request.getUserID() + " and \"itemID\" = \""
                        + request.getItemID() + "\"";
            } else {
                query = "INSERT INTO ShoppingCart VALUES(" + request.getUserID() + ", \"" + request.getItemID()
                        + "\", " + request.getQuantity() + ")";
            }
            connection.createStatement().execute(query);
            connection.close();
            responseObserver.onNext(Void.newBuilder().build());
        } catch (SQLException exception) {
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void removeItemFromShoppingCart(ItemToCartRequest request, StreamObserver<Void> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToCustomerDB();
            String query = "SELECT quantity from ShoppingCart where \"userID\" = " + request.getUserID()
                    + " and \"itemID\" = \"" + request.getItemID() + "\"";
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            boolean itemExists = resultSet.next();
            int currentCartValueForItem = 0;
            int quantity = request.getQuantity();
            if (itemExists) {
                currentCartValueForItem = resultSet.getInt(1);
            }
            if (currentCartValueForItem == 0 || currentCartValueForItem < request.getQuantity()) {
                responseObserver.onNext(Void.newBuilder().build());
                responseObserver.onCompleted();
                return;
            } else if (currentCartValueForItem == quantity) {
                query = "DELETE FROM ShoppingCart WHERE \"userID\" = " + request.getUserID() + " and \"itemID\" = \""
                        + request.getItemID() + "\"";
            } else {
                query = "UPDATE ShoppingCart SET \"quantity\" = " + (currentCartValueForItem - quantity)
                        + " WHERE \"userID\" = " + request.getUserID() + " and \"itemID\" = \""
                        + request.getItemID() + "\"";
            }
            connection.createStatement().execute(query);
            connection.close();
            responseObserver.onNext(Void.newBuilder().build());
        } catch (SQLException exception) {
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void clearShoppingCart(UserID request, StreamObserver<Void> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToCustomerDB();
            String query = "DELETE from ShoppingCart where \"userID\" = " + request.getUserId();
            connection.createStatement().execute(query);
            connection.close();
            responseObserver.onNext(Void.newBuilder().build());
        } catch (SQLException exception) {
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void displayShoppingCart(UserID request, StreamObserver<SaleItem> responseObserver) {
        try {
            Connection connectionToCustomerDB = Utils.getConnectionToCustomerDB();
            Connection connectionToProductDB = Utils.getConnectionToProductDB();
            String query = "SELECT itemID, quantity from ShoppingCart where \"userID\" = " + request.getUserId();
            ResultSet shoppingCartResultSet = connectionToCustomerDB.createStatement().executeQuery(query);
            while (shoppingCartResultSet.next()){
                int itemID = shoppingCartResultSet.getInt(1);
                int quantity = shoppingCartResultSet.getInt(2);
                query = "SELECT \"itemName\",\"category\",\"keyWords\",\"isNew\",\"itemPrice\",\"sellerID\",\"quantity\""
                        + " FROM Products WHERE \"itemID\" = " + itemID;
                ResultSet productResultSet = connectionToProductDB.createStatement().executeQuery(query);
                SaleItem saleItem = SaleItem.newBuilder()
                        .setItemName(productResultSet.getString(1))
                        .setCategory(productResultSet.getInt(2))
                        .setKeyWords(productResultSet.getString(3))
                        .setIsNew(productResultSet.getInt(4))
                        .setItemPrice(productResultSet.getFloat(5))
                        .setSellerID(productResultSet.getInt(6))
                        .setQuantity(quantity)
                        .build();
                responseObserver.onNext(saleItem);
            }
            connectionToCustomerDB.close();
            connectionToProductDB.close();
        } catch (SQLException exception) {
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }
}
