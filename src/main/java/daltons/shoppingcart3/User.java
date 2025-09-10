package daltons.shoppingcart3;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String name, surname, email, password;
    private List<String> addresses;
    private List<CardDetail> cardDetails;
    private List<Order> orderHistory;

    public User(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.addresses = new ArrayList<>();
        this.cardDetails = new ArrayList<>();
        this.orderHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean authenticate(String email, String password) {
        // Şifre hashlenecek, bunu unutma.
        try {
            return this.email.equals(email) && this.password.equals(hashPassword(password));            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void printAddresses() {
        int i = 1;
        for (String address : this.addresses) {
            System.out.println("Address " + i++ + ": " + address);
        }
    }

    public List<String> getAddresses() {
        return this.addresses;
    }

    public String getAddress(int index) {
        return this.addresses.get(index);
    }

    public void addAddress(String address) {
        this.addresses.add(address);
    }

    public void editAddress(int index, String address) {
        this.addresses.set(index, address);
    }

    public void removeAddress(int index) {
        this.addresses.remove(index);
    }

    public void addCardDetail(CardDetail cardDetail) {
        this.cardDetails.add(cardDetail);
    }

    public void printCardDetails() {
        int i = 1;
        for (CardDetail cardDetail : this.cardDetails) {
            System.out.println("Card " + i++ + ": " + cardDetail.toString());
        }
    }

    public List<CardDetail> getCardDetails() {
        return this.cardDetails;
    }

    public String getCardDetail(int index) {
        return this.cardDetails.get(index).toString();
    }

    public String getCardNumber(int index) {
        return this.cardDetails.get(index).getCardNumber();
    }

    public void setCardDetail(int index, CardDetail cardDetail) {
        this.cardDetails.set(index, cardDetail);
    }

    public void removeCardDetail(int index) {
        this.cardDetails.remove(index);
    }

    public void addOrderHistory(Order order) {
        this.orderHistory.add(order);
    }

    public List<Order> getOrderHistory() {
        return this.orderHistory;
    }

    public void printOrderHistory() {
        for (Order order : this.orderHistory) {
            System.out.println(order.toString());
        }
    }

    public static String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String hex = bytesToHex(hash);
        return hex;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", addresses=" + addresses +
                ", cardDetails=" + cardDetails +
                ", orderHistory=" + orderHistory +
                '}';
    }
}
