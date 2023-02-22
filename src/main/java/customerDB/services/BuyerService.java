package customerDB.services;

import com.example.DistributedAssignment.services.*;
import com.example.DistributedAssignment.services.Void;
import common.Utils;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BuyerService extends BuyerItemServicesGrpc.BuyerItemServicesImplBase {
    @Override
    public void getBuyerPurchaseHistory(UserID request, StreamObserver<PurchaseHistoryResponse> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToCustomerDB();
            String query = "SELECT \"itemID\", \"quantity\", \"timestamp\" , \"purchaseID\", \"feedback\" from PurchaseHistory where \"userID\" = " + request.getUserId();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            while (resultSet.next()){
                PurchaseHistoryResponse purchaseHistoryResponse = PurchaseHistoryResponse.newBuilder()
                        .setItemID(resultSet.getInt(0))
                        .setQuantity(resultSet.getInt(1))
                        .setTimestamp(resultSet.getInt(2))
                        .setPurchaseID(resultSet.getInt(3))
                        .setFeedback(resultSet.getFloat(4))
                        .build();
                responseObserver.onNext(purchaseHistoryResponse);
            }
            connection.close();
        } catch (SQLException exception){
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void provideFeedback(FeedbackRequest request, StreamObserver<Void> responseObserver) {
        try {
            Connection connectionToCustomerDB = Utils.getConnectionToCustomerDB();
            String query = "SELECT sellerID, feedback from PurchaseHistory where \"purchaseID\" = " + request.getPurchaseID();
            ResultSet resultSet = connectionToCustomerDB.createStatement().executeQuery(query);
            if(resultSet.next() && resultSet.getInt(2)==0){
                query = "UPDATE PurchaseHistory SET \"feedback\" = " + resultSet.getInt(2)
                        + "WHERE \"purchaseID\" = " + resultSet.getInt(1);
                connectionToCustomerDB.createStatement().executeQuery(query);

                updateSellerRating(connectionToCustomerDB, resultSet.getInt(1), resultSet.getInt(2));
            } else {
                responseObserver.onNext(Void.newBuilder().build());
            }
            connectionToCustomerDB.close();
        } catch (SQLException exception){
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }

    private void updateSellerRating(Connection connectionToCustomerDB, int feedback, int sellerID) throws SQLException  {
        String ratingField = "";
        if(feedback==1){
            ratingField = "thumbsUp";
        } else {
            ratingField = "thumbsDown";
        }
        String query = "SELECT "+ratingField+" FROM Sellers WHERE \"sellerID\" = "+sellerID;
        ResultSet resultSet = connectionToCustomerDB.createStatement().executeQuery(query);
        int newRating = resultSet.getInt(1)+1;

        query = "UPDATE Sellers SET \""+ ratingField+"\" = "+newRating+"WHERE \"sellerID\" = "+sellerID;
        connectionToCustomerDB.createStatement().executeQuery(query);
    }

    @Override
    public void searchItemsForSale(SearchRequest request, StreamObserver<SaleItem> responseObserver) {
        try {
            Connection connection = Utils.getConnectionToProductDB();
            String keywords = request.getKeywords();
            String[] keywordsList = keywords.split(":");
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT \"itemName\",\"category\",\"keyWords\",\"isNew\",\"itemPrice\",\"sellerID\"," +
                    "\"quantity\" FROM Products where \"category\" = ").append(request.getCategory()).append(" AND (");
            for (String keyword : keywordsList) {
                builder.append("\" keywords\" LIKE \"%").append(keyword).append("%\"");
                builder.append(" OR ");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
            String query = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            while(resultSet.next()){
                SaleItem saleItem = SaleItem.newBuilder()
                        .setItemName(resultSet.getString(1))
                        .setCategory(resultSet.getInt(2))
                        .setKeyWords(resultSet.getString(3))
                        .setIsNew(resultSet.getInt(4))
                        .setItemPrice(resultSet.getFloat(5))
                        .setSellerID(resultSet.getInt(6))
                        .setQuantity(resultSet.getInt(7))
                        .build();
                responseObserver.onNext(saleItem);
            }
            connection.close();
        } catch(SQLException exception){
            responseObserver.onError(exception);
        }
        responseObserver.onCompleted();
    }
}
