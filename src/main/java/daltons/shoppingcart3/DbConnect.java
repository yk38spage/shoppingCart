package daltons.shoppingcart3;

public class DbConnect {
    private static final String URL = "JDBC_URL_HERE";
    private static final String USER = "DB_USERNAME_HERE";
    private static final String PASSWORD = "DB_PASSWORD_HERE";

    public static String getURL() {
        return URL;
    }

    public static String getUSER() {
        return USER;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }
}
