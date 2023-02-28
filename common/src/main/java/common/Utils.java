package common;

import com.example.DistributedAssignment.services.SaleItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface Utils {

    int CUSTOMER_DB_PORT = 5003;

    int PRODUCT_DB_PORT = 5004;

    int SERVER_SIDE_SELLER_INTF_PORT = 5005;

    int SERVER_SIDE_BUYER_INTF_PORT = 5006;

    static int fetchUserIDGRPC(String username, String password) throws SQLException {
        Connection fetchConnection = getConnectionToCustomerDB();
        String sql = "SELECT userID FROM Login WHERE \"username\" = \"" + username + "\" and \"password\" = " + "\"" + password + "\"";
        ResultSet resultSet = fetchConnection.createStatement().executeQuery(sql);
        resultSet.next();
        int userID = resultSet.getInt(1);
        fetchConnection.close();
        return userID;
    }

    static String getResponseFromSaleItemsIterator(Iterator<SaleItem> values){
        JSONArray jsonArray = new JSONArray();

        while (values.hasNext()) {
            SaleItem value = values.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sellerID", value.getSellerID());
            jsonObject.put("category", value.getCategory());
            jsonObject.put("keyWords", value.getKeyWords());
            jsonObject.put("itemName", value.getItemName());
            jsonObject.put("isNew", value.getIsNew());
            jsonObject.put("itemPrice", value.getItemPrice());
            jsonObject.put("quantity", value.getQuantity());
            jsonObject.put("itemID", value.getItemID());
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

    static List<SaleItemPojo> getSaleItemsForRestResponse(Iterator<SaleItem> values){
        List<SaleItemPojo> saleItems = new ArrayList<>();

        while (values.hasNext()) {
            SaleItem value = values.next();
            SaleItemPojo saleItem = new SaleItemPojo();
            saleItem.setSellerID(value.getSellerID());
            saleItem.setItemID(value.getItemID());
            saleItem.setItemName(value.getItemName());
            saleItem.setItemPrice(value.getItemPrice());
            saleItem.setQuantity(value.getQuantity());
            saleItem.setIsNew(value.getIsNew());
            saleItem.setKeyWords(value.getKeyWords());
            saleItems.add(saleItem);
        }

        return saleItems;
    }

    static float fetchSellerRatingGRPC(int sellerID) throws SQLException {
        Connection connection = getConnectionToCustomerDB();
        String query = "SELECT thumbsUp, thumbsDown from Sellers where sellerID = " + sellerID;
        ResultSet resultSet = connection.createStatement().executeQuery(query);
        float rating = -1;
        if(resultSet.next()){
            int thumbsUp = resultSet.getInt(1);
            int thumbsDown = resultSet.getInt(1);

            if (thumbsUp + thumbsDown != 0) {
                rating = 10 * (thumbsUp - thumbsDown) / ((float) (thumbsUp + thumbsDown));
            }
        }
        connection.close();
        return rating;
    }


    static Connection getConnectionToCustomerDB() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:/Users/ankursharma/IdeaProjects/DistributedSystems/DistributedAssignment2/src/main/java/customerDB/CustomerDB.db");
    }

    static Connection getConnectionToProductDB() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:/Users/ankursharma/IdeaProjects/DistributedSystems/DistributedAssignment2/src/main/java/productDB/ProductDB.db");
    }
}
