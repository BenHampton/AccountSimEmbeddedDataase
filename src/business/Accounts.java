
package business;

import java.util.ArrayList;

/**
 *
 * @author Ben Hampton
 */
public interface Accounts {
    public int getAcctID(); // retrieves accountID --> allows for multiple accountTYPEs under one ID
    public int getAcctNo(); //retrieves account number
    public String getName(); //retrieves Account Name
    public double getBalance(); //retieves account balance
    public String getUserName(); // retrieves Accounts UserName
    public String getAdmin(); // retrieves Account Admin (YES/NO) 
    public String getPwd();
    public ArrayList<String> getLog();//retrieves account lot to display in database
    
    public void setUpdate(String nm, String userName, String admin, String pwd);
    public void setPayment(double amt); //retrieves Payment
    public void setCharge(double amt, String desc); //retrieves charge
    public void setInterest(double amt); // retrives Interest
    
    
    public String getTypeCd(); // account type code (SV,MM,CK)
    public String getTypeDesc(); //account type description('Saving Account', 'Money Merket', 'Checking Account')
    
    public String getErrMsg();//displays Error Msg if->ERROR
    public String getActionMsg();//displays Action when Action is occured
}
