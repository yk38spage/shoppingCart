package daltons.shoppingcart3;

public interface CanQuery {
    public String getDB(String tableName);
    public String getDBWhereQuery(String tableName);

    public void initializeProducts();
    public void initializeUsers();

    public void loginPage();
    public void registerPage();
    public void checkoutPage();
}
