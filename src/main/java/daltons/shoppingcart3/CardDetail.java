package daltons.shoppingcart3;

import java.util.Base64;

public class CardDetail {
    String cardNumber, cvv, expiryDate;
    
    public CardDetail(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getCvv() {
        return Integer.parseInt(decodeData(cvv));
    }


    public String getExpiryDate() {
        return expiryDate;
    }

    public static String encodeData(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static String decodeData(String data) {
        return new String(Base64.getDecoder().decode(data));
    }

    @Override
    public String toString() {
        return "Card Number: " + decodeData(cardNumber) + ", Expiry Date: " + decodeData(expiryDate) + ", CVV: " + decodeData(cvv);
    }
}
