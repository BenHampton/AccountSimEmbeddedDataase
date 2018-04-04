package accounts;

/**
 *
 * @author Ben Hampton
 */
public class Main {
    public static void Main(String arg[]){
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new AccountView().setVisible(true);
        }
    });
    }
    
}
