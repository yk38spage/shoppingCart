package daltons.shoppingcart3;

import java.util.Map;

public class Cloth extends Product {
    public Cloth(int id, String name, String category, double price) {
        super(id, name, category, price);
    }

    public Cloth(int id, String name, String category, double price, Map<String, String> specifications) {
        super(id, name, category, price, specifications);
    }

    @Override
    public String getDetails() {
        return "Cloth: " + name + ", Price: $" + price + ", Specs: " + specifications;
    }
}
