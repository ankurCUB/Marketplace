package productDB;

import com.example.DistributedAssignment.services.*;
import com.example.DistributedAssignment.services.Void;
import common.Utils;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaleItemServices extends SaleItemServicesGrpc.SaleItemServicesImplBase {

    @Override
    public void putItemForSale(SaleItem request, StreamObserver<Void> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToProductDB();
            String query = "INSERT INTO Products(\"itemName\",\"category\",\"keyWords\",\"isNew\",\"itemPrice\",\"sellerID\",\"quantity\") VALUES (\""
                    + request.getItemName() + "\", " + request.getCategory() + ", \"" + request.getKeyWords() + "\", " + request.getIsNew() + ", "
                    + request.getItemPrice() + ", " + request.getSellerID() + ", " + request.getQuantity() + ")";
            connection.createStatement().executeQuery(query);
            connection.close();
            responseObserver.onNext(Void.newBuilder().build());
            responseObserver.onCompleted();
        } catch (SQLException exception) {
            responseObserver.onError(exception);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void changeSalePriceOfItem(ChangeSalePriceRequest request, StreamObserver<Void> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToProductDB();
            String query = "UPDATE Products SET \"itemPrice\" ="+request.getNewPrice()+" WHERE \"itemID\" = "
                    +request.getItemID()+" AND \"sellerID\" = "+request.getSellerID();
            connection.createStatement().executeQuery(query);
            connection.close();
            responseObserver.onNext(Void.newBuilder().build());
            responseObserver.onCompleted();

        } catch (SQLException exception){
            responseObserver.onError(exception);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void removeItemFromSale(RemoveItemFromSaleRequest request, StreamObserver<Void> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToProductDB();
            String query = "SELECT quantity FROM Products WHERE \"itemID\" = "+request.getItemID()+" AND \"sellerID\" = "+request.getSellerID();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            int currentSaleQuantityForItem = 0;
            int quantity = request.getQuantity();
            if(resultSet.next()){
                currentSaleQuantityForItem = resultSet.getInt(1);
                int sellerID = request.getSellerID();
                int itemID = request.getItemID();
                if(currentSaleQuantityForItem<quantity){
                    responseObserver.onNext(Void.newBuilder().build());
                    responseObserver.onCompleted();
                    connection.close();
                    return;
                } else if(currentSaleQuantityForItem==quantity){
                    query = "DELETE FROM Products WHERE \"sellerID\" = "+sellerID+" and \"itemID\" = \""+itemID+"\"";
                } else{
                    query = "UPDATE ShoppingCart SET \"quantity\" = "+(currentSaleQuantityForItem-quantity) + " WHERE \"sellerID\" = "+sellerID+" and \"itemID\" = \""+itemID+"\"";
                }
                connection.createStatement().executeQuery(query);
                connection.close();
            }
            responseObserver.onNext(Void.newBuilder().build());
            responseObserver.onCompleted();

        } catch (SQLException exception){
            responseObserver.onError(exception);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void displayItemsOnSale(UserID request, StreamObserver<SaleItem> responseObserver) {
        try{
            Connection connection = Utils.getConnectionToProductDB();
            String query = "SELECT itemName,category,keyWords,isNew,itemPrice,sellerID,quantity FROM Products WHERE \"sellerID\" = "+request.getUserId();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            while (resultSet.next()){
                SaleItem saleItem = SaleItem.newBuilder()
                        .setItemName(resultSet.getString(1))
                        .setCategory(resultSet.getInt(2))
                        .setKeyWords(resultSet.getString(3))
                        .setIsNew(resultSet.getInt(4))
                        .setItemPrice(resultSet.getFloat(5))
                        .setSellerID(resultSet.getInt(6))
                        .setQuantity(resultSet.getInt(7))
                        .setItemID(request.getUserId())
                        .build();
                responseObserver.onNext(saleItem);
            }
            connection.close();
        } catch (SQLException exception){
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }
}
