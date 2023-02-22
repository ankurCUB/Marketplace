package seller;

import com.example.DistributedAssignment.services.SaleItem;

public interface SellersInterface {
    String createAccount(String username, String password, String name);
    String login(String username, String password);
    String logout(int sellerID);
    String getSellerRating(int sellerID);
    String putItemForSale(SaleItem item);
    String changeSalePriceOfItem(int sellerID, int itemID, float newPrice);
    String removeItemFromSale(int sellerID, int itemID, int quantity);
    String displayItemsOnSale(int sellerID);
}