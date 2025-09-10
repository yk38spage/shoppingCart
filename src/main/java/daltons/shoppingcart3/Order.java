package daltons.shoppingcart3;

import java.util.List;

public class Order {
    private int orderId;
    private List<Product> products;
    private String status;

    public Order(int orderId, List<Product> products) {
        this.orderId = orderId;
        this.products = products;
        this.status = "Processing";
    }

    public Order(int orderId, List<Product> products, String status) {
        this.orderId = orderId;
        this.products = products;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ID: #" + orderId + "\n" +
               "Products: " + products + "\n" +
               "Status: " + status + "\n";
    }
}
