package daltons.shoppingcart3;

import java.util.HashMap;
import java.util.Map;

public abstract class Product {
    protected int id;
    protected String name;
    protected String category;
    protected double price;
    protected Map<String, String> specifications;

    public Product(int id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.specifications = new HashMap<>();
    }

    public Product(int id, String name, String category, double price, Map<String, String> specifications) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.specifications = new HashMap<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public Map<String, String> getSpecifications() { return specifications; }

    public void addSpecification(String key, String value) {
        specifications.put(key, value);
    }

    @Override
    public String toString() {
        return "\nProduct ID: " + id + ", Name: " + name + ", Price: " + price;
    }

    public abstract String getDetails();
}
