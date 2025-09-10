/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package daltons.shoppingcart3;

/**
 *
 * @author yigit
 */
public class Shoppingcart3 {

    public static void main(String[] args) {
        // TODO code application logic here
        
        Shoppingcart3 app = new Shoppingcart3();
        app.start();
    }

    public void start() {
        Menus menus = new Menus();

        while (true) {
            if (menus.getLoggedInUser() != null) {
                menus.showMainMenu();
            } else {
                menus.showLoginMenu();
            }
        }
    }
}
