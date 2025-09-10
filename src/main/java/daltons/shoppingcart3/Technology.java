package daltons.shoppingcart3;

import java.util.Map;

public class Technology extends Product {
    private final double TAX_RATE = 0.2;

    public Technology(int id, String name, String category, double price) {
        super(id, name, category, price);
    }

    public Technology(int id, String name, String category, double price, Map<String, String> specifications) {
        super(id, name, category, price, specifications);
    }

    private String getTax() {
        return String.format("%.2f", price * TAX_RATE);
    }

    private String calculatePriceWithoutTax() {
        return String.format("%.2f", price - (price * TAX_RATE));
    }

    @Override
    public String getDetails() {
        return String.format("Technologic Device: %s, Price without tax: $%s, Tax: $%s, Price: $%.2f, Specs: %s", name, calculatePriceWithoutTax(), getTax(), price, specifications);
    }
}
