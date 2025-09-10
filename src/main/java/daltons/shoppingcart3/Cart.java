package daltons.shoppingcart3;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Product> items = new ArrayList<>();
    double totalPrice = 0.0;

    public void addProduct(Product product) {
        items.add(product);
        totalPrice += product.getPrice();
    }

    public void addProduct(int productId, List<Product> products) {
        for (Product p : products) {
            if (p.getId() == productId) {
                addProduct(p);
                break;
            }
        }
    }

    public void removeProduct(Product product) {
        items.remove(product);
        totalPrice -= product.getPrice();
    }
    
    public List<Product> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
