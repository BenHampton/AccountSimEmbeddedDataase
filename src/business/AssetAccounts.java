package business;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
//import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author Ben Hampton
 */
public abstract class AssetAccounts implements Accounts{
    private int acctno, acctid;
    private String errmsg, actmsg, nm, dtype, pwd, username, admin;
    private double balance, pmt, chrg, intrate;
    //private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //private String URL ="jdbc:derby://localhost:1527/AcctDB"; //sqlURL
    //private String user = "sqlhampton";
    //private String password = "sesame";
    
//    private String URL ="jdbc:derby:AccountDB;create=true";//Enbedded URL
//    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";//Enbedded URL
//    private String user = "bhampton";//Enbedded URL
//    private String password = "sesame";//Enbedded URL
    
    private String URL ="jdbc:sqlite:AccountEmbeddedDB.sqlite";//sqlite URL
    private String driver = "org.sqlite.JDBC";//sqlite URL
    
    Statement s;
    Connection conn;
    NumberFormat curr = NumberFormat.getCurrencyInstance();
    DecimalFormat df = new DecimalFormat("#.00");
    //constructor for CREATING NEW ACCOUNT
    public AssetAccounts(String nm, double startbal, String psswd, String dtype){
        this.errmsg = "";
        this.actmsg ="";
        /*
            -create Rnadom AccountID
            -If Exxistis retry AccountID  
            -create random acctno
            -IF available set Gobals
            - USERNAME is set to FIRST letter in fname and FULL lname
            -Create New Tabel for User for (Payment,Charge,ChargeDesc)
            - set globals
            -IF NOT accto==0 and retry with random again
            */   
        try{
            //Connect to Database
            Class.forName(driver).newInstance();
            this.conn = DriverManager.getConnection(URL);
            this.s = conn.createStatement();
            if(this.conn == null){
                this.errmsg = "No Connection";
                return;
            }
            while(this.acctid == 0){
                //set AccountID
                this.acctid = (int) (Math.random() * 1000000);
                String sqlID = "Select AccountID from Account WHERE AccountID = " + this.acctid;
                ResultSet rsID = s.executeQuery(sqlID);
                //check is AccountID Exisits
                if(rsID.next()){
                    this.acctid = 0;
                }
            }//end of while
            //create Accountnumber
            while(this.acctno == 0){ 
                //set this.accno to a number * 1000000
                this.acctno = (int) (Math.random() * 1000000);
                String sql = "SELECT AccountNumber FROM Account WHERE AccountNumber = " + this.acctno;                 
                ResultSet rs = s.executeQuery(sql);
                //if no acctNO found GOOD
                if(rs.next()){
                    this.acctno = 0;
                    this.balance = 0;
                }else if(!rs.next()){
                    //inizilize globals
                    this.nm = nm;
                    this.balance = startbal;
                    this.dtype = dtype;
                    this.pwd = psswd;
                    this.admin = "YES";
                    //create Username
                    this.username = createUserName(nm);
                    if(this.errmsg.isEmpty()){
                        //add new row to Databasse using globals
                        String sqlNewAcct = "INSERT INTO Account (AccountID, Admin, AccountNumber, AccountType, AcctBalance, AcctName, Password, UserName)" +
                                      "values("+this.acctid+", '"+this.admin+"', "+this.acctno+",'"+ this.dtype+"',"+this.balance+",'"+this.nm+"', '"+this.pwd+"', '"+this.username+"')";
                        //if sql is executed set actmsg else errmsg
                        s.execute(sqlNewAcct);
                        this.actmsg = "Account: " + this.acctno + " has been created.";
                        //create new tabel in AccountBD for NEW Acct User
                        setCreateUserTBL();
                        rs.close();
                    }else{
                        this.acctno = -1;
                        this.balance = 0;
                        this.errmsg = "Account Opening Error";
                    }
                }else{
                //if rs.next() -> acctNo found this.acctno=0 
                this.acctno = -1;
                }
            }//end of while AccountNumber
        }catch(Exception e){
            this.errmsg = "SQL Exception: " + e.getMessage();
            this.acctno = -1;
        }//end of try-catch(outter)
    }//end of cusnstuctor (String nm, double startval, String pwd, String dtype)
    
    //cuonstructor for OPENING EXSISTING ACCOUNT
    public AssetAccounts(String acctno, String dtype){
        try{
            //connect to Database
            Class.forName(driver).newInstance();
            this.conn = DriverManager.getConnection(URL);
            this.s = conn.createStatement();            
            //sql query to selecte Existing AcctNo
            String sql = "SELECT AccountID, Admin, AccountNumber, AcctName, AcctBalance, AccountType, UserName, Password FROM Account WHERE AccountNumber = "+ acctno;
            ResultSet rs = s.executeQuery(sql);
            if(rs.next()){
                //set globals to parameters
                this.acctid = rs.getInt("AccountID");
                this.admin = rs.getString("Admin");
                this.acctno = rs.getInt("AccountNumber");
                this.nm = rs.getString("AcctName");
                this.balance = rs.getDouble("AcctBalance");
                this.dtype = rs.getString("AccountType");
                this.username = rs.getString("UserName");
                this.pwd = rs.getString("Password");
                rs.close();
            }else{
                this.errmsg = "Error occured opening account";
            }            
        }catch(Exception e){
            this.errmsg = "Error: " + e.getMessage();
        }
    }//end of cunstructor (int acctno, String dtype)
    
    //constructor for CREATING ADDITTIONAL ACCOUNT /w EXISTING ACCOUNT
    public AssetAccounts(int acctid, String nm, double startval, String passwd, String dtype){
        this.errmsg = "";
        this.actmsg = "";
        try{
            //connect to database
            Class.forName(driver);
            this.conn = DriverManager.getConnection(URL);
            this.s = conn.createStatement();
            //add newAccount with acctID
            //create Accountnumber
            while(this.acctno == 0){ 
                //set this.accno to a number * 1000000
                this.acctno = (int) (Math.random() * 1000000);
                String sql = "SELECT AccountNumber FROM Account WHERE AccountID = " + this.acctid;                 
                ResultSet rs = s.executeQuery(sql);
                //check if acctID has new acctNumber
                while(rs.next()){
                    if(this.acctno == (rs.getInt("AccountNumber"))){
                        this.acctno = 0;
                    }
                }
                if(this.acctno != 0){
                    //inizilize globals
                    this.acctid = acctid;
                    this.nm = nm;
                    this.balance = startval;
                    this.dtype = dtype;
                    this.pwd = passwd;
                    this.admin = "NO";
                    //create Username
                    this.username = createUserName(nm);
                    if(this.errmsg.isEmpty()){
                        //add new row to Databasse using globals
                          String sqlAddAcct = "INSERT INTO Account (AccountID, Admin, AccountNumber, AccountType, AcctBalance, AcctName, Password, UserName)" +
                                      "values("+this.acctid+", '"+this.admin+"', "+this.acctno+",'"+ this.dtype+"',"+this.balance+",'"+this.nm+"', '"+this.pwd+"', '"+this.username+"')";
                        //if sql is executed set actmsg else errmsg
                        s.execute(sqlAddAcct);
                        this.actmsg = "Account: " + this.acctno + " has been created.";
                        //create new tabel in AccountBD for NEW Acct User
                        setCreateUserTBL();
                        rs.close();
                        this.actmsg = "Add Account: " +this.dtype+this.acctno + " to Account ID: " + this.acctid;
                    }else{
                        this.acctno = -1;
                        this.balance = 0;
                        this.errmsg = "Account Opening Error";
                    }
                }
            }//end of while AccountNumber
        }catch(Exception e){
            this.errmsg = "Adding Additional Account Error: " + e.getMessage(); 
            String t = "";
        }//end of try-catch
    }//end of cunstructor (int acctid, String nm, double startval, String pwd, String dtype)
    
    private String createUserName(String uname){
        //create userName = sql + math.random
        //Test UserName against DB
        //if exsits REDO (math.random)
        //else create table
        String n = this.nm;
        int num = 0;
        //n lenght (11)
        int len = n.length();
        int nIndex=0;
        //(fL) FirstName = first letter -> toUpperCase
        String fL = n.substring(0, 1).toUpperCase();
        //run through this.nm
        for(int i = 0; i < len; i++){
            //if "space" found seir nIndex= i-1 off set for substring
            if(n.charAt(i) == ' '){
                nIndex = i+1;
            }
        }
        while(num == 0){
            //add random number to end uf username
            num = (int) (Math.random() * 100);
            //Full Last Name to lower case at 'space'
            String lower = n.substring(nIndex).toLowerCase();
            // fL = First Leter of firstName -- lower = Full LastName ALL lower case + randomNumber
            uname = fL+lower+ Integer.toString(num);
            //check if availibe
            try{
                String sql = "SELECT UserName "
                            + "FROM Account "
                            + "WHERE UserName = '"+uname+"' "
                            + "AND AccountNumber = " + this.acctno;
                ResultSet rs = s.executeQuery(sql);
                if(rs.next()){
                    num = 0;
                }
            }catch(SQLException e){
                this.errmsg = "Error creating UserName: " + e.getMessage();
            }
        }//end of while
        return uname;
    }
    
    //retrieves payment
    @Override
    public void setPayment(double amt){
        this.errmsg="";
        this.actmsg ="";
        this.pmt = amt;
        String val = "Payment";
        try{
            //validate for payment so its not neg or above this.balance
            if(this.pmt <= 0){
                this.errmsg = "Payment must be greater that ZERO(0)";
            }
            else{
                if(this.errmsg.isEmpty()){
                    this.balance = this.balance + this.pmt;
                    this.actmsg = "Account: " + this.acctno + " has received a payment of " + this.pmt;
                    //getDB(this.actmsg)
                    //UPDATE AccountDB
                     String sql = "update Account "
                             + "set AcctBalance = "+this.balance
                             + " where AccountNumber = "+this.acctno;
                    s.execute(sql);
                    
                    //INSERT INTO sql statement
                    setAccountAction(this.pmt, val);
                }else{
                    this.balance -= this.pmt;
                }
            }
        }catch(NumberFormatException e){
            this.errmsg = "Payment Error: " + e.getMessage();
        }catch(SQLException e){
            this.errmsg = "SQL Error: " + e.getMessage();
        }
        this.pmt = 0;
    }//end of setPayment
    
    //retrieves charge
    @Override
    public void setCharge(double amt, String d){
        this.chrg = amt;
        this.errmsg = "";
        String desc = d;
        String val = "Charge";
        try{
            if(desc.isEmpty()){
                this.errmsg = "Chrage Description Message is empty";
            }
            //Validate charge for positive charge amount and sufficent funds in acct for charge
            if(this.chrg < 0){
                this.errmsg = "DECLINED: Charged amount must be greater than ZERO(0)";
            }else if(this.chrg > this.balance){
                this.errmsg = "DECLINED: Insufficent funds on Account: " 
                                + this.acctno + " for charge of " + this.pmt;
            }else{
                //validation passed
                this.balance -= this.chrg;
                //check if emsg is empty
                if(this.errmsg.isEmpty()){
                    this.actmsg = "Account: " + this.acctno +", Succsessful charge of: " + this.chrg;
                    //add charge to AccountsDB 
                    String sql= "UPDATE Account "
                            + "SET AcctBalance = AcctBalance - "+this.chrg+" "
                            + "WHERE AccountNumber = "+this.acctno;
                    s.execute(sql);
                    setAccountAction(this.chrg, val, desc);
                }else{
                    //is error add charge back to balance
                    this.balance += this.chrg;
                }
            }
            //set charge back to 0
            this.chrg = 0;
        }catch(NumberFormatException e){
            this.errmsg = "Error: " + e.getMessage();
        }catch(SQLException e){
            this.errmsg = "SQL Error: " + e.getMessage();
        }
    }//end of setCharge(double amt)
    
    // retrives Interest
    @Override
    public void setInterest(double ir){
        this.errmsg="";
        this.actmsg="";
        double intearn = 0;
        String val = "InterestRate";
        NumberFormat pct = NumberFormat.getPercentInstance();
        //validate
        try{
            if(ir <= 0 || ir > 1.0){
                this.errmsg = "Invaild Interest Rate: must be greater than 0 and less tha 1.0";
            }else{
                this.intrate = this.balance * ir/12;
                intearn = Double.parseDouble(df.format(this.intrate));
                this.balance += this.intrate;
                if(this.errmsg.isEmpty()){
                    this.actmsg = "Intrest Rate of " + curr.format(this.intrate) +
                            " for month at annual rate of " + pct.format(ir);
                    //SQL
                    String sql = "UPDATE Account "
                                + "SET AcctBalance = AcctBalance + " + intearn 
                                + " WHERE AccountNumber = "+this.acctno;
                    s.execute(sql);
                    setAccountAction(intearn, val);
                }else{
                    this.balance -= this.intrate; 
                }
            }
        }catch(NumberFormatException e){
            this.errmsg = "Error: " + e.getMessage();
        }catch(SQLException e){
            this.errmsg = "SQL Error: " + e.getMessage();
        }    
    }//end of InterestRate
    
    // Create New Table In DataBase for User
    private void setCreateUserTBL(){
        try{
            //create table for Account
            String sql = "CREATE TABLE "+ this.dtype + this.username
                        + "(AccountID int, "
                        + "Admin varchar(5), "
                        + "AccountNumber int, "
                        + "AccountName varchar(35), "
                        + "Balance double, "
                        + "Type varchar(15), "
                        + "Payment double, "
                        + "Charge double, "
                        + "ChargeDescription varchar(45), "
                        + "InterestRate double)";
            s.execute(sql);
            //Insert Account information from Accounts into user Account
            String sqlINSERT = "INSERT INTO "+this.dtype+this.username+" (AccountID, Admin, AccountNumber, Type, AccountName, Balance) "
                            + "SELECT Account.AccountID, Account.Admin, Account.AccountNumber, Account.AccountType, Account.AcctName, Account.AcctBalance "
                            + "FROM Account "
                            + "WHERE AccountNumber = "+this.acctno;
            s.execute(sqlINSERT);
        }catch(Exception e){
            this.errmsg = ("Error with New Account: " + getAcctNo());
        }
    }//end of createUser
    
    private void setAccountAction(double amt, String val){
        try{
            //INSERT INTO
            String sql = "INSERT INTO "+this.dtype+this.username+ " (AccountID, Admin, AccountNumber, AccountName ,Type, "+val+", Balance) "
                        + "values ("+this.acctid+", '"+this.admin+"', "+this.acctno+", '"+this.nm+"', '"+this.dtype+"', "+amt+" , (SELECT AcctBalance FROM Account WHERE AccountNumber = "+this.acctno+"))";
            s.execute(sql);
        }catch(SQLException e){
            this.errmsg = "Account Action Error in DataBase";
            //UNDO ACTION in APP.Accounts
        }
    }
    private void setAccountAction(double amt, String val, String d){
        try{
            //INSERT INTO
            String sql = "INSERT INTO "+this.dtype+this.username+ " (AccountID, Admin, AccountNumber, AccountName ,Type, "+val+", ChargeDescription, Balance) "
                        + "values ("+this.acctid+", '"+this.admin+"', "+this.acctno+",'"+this.nm+"', '"+this.dtype+"', "+amt+", '"+d+"' , (select AcctBalance from Account where AccountNumber = "+this.acctno+"))";
            s.execute(sql);
        }catch(SQLException e){
            this.errmsg = "Account Action Error in DataBase";
            //UNDO ACTION in Accounts
        }
    }
    

    @Override
    public ArrayList<String> getLog(){
        //validate
        this.actmsg = "";
        this.errmsg = "";
        ArrayList<String> l = new ArrayList<>();
        try{
            String sql = "SELECT Charge, ChargeDescription, Payment "
                        + "FROM "+this.dtype+this.username+" "
                        + "WHERE AccountNumber = "+this.acctno;
            ResultSet rs = s.executeQuery(sql);
            while(rs.next()){
                if(rs.getString("ChargeDescription") != null && rs.getString("Charge") != null){
                    l.add("Charge of " + rs.getString("Charge") +" for "+ rs.getString("ChargeDescription"));
                }else if(rs.getString("Payment") != null){
                    l.add("Payment of " + rs.getString("Payment"));
                }
            }
            rs.close();
        }catch(SQLException e){
            this.errmsg = "Error retrieving Account ChargeDescripton: " + e.getMessage();
        }
        return l;
    }
    
    @Override
    public void setUpdate(String nm, String userName, String admin, String passwd){
        String cuname = this.username;
        this.errmsg = "";
        this.actmsg = "";
        try{
            this.conn = DriverManager.getConnection(URL);
            this.s = this.conn.createStatement();
            //get info from database X
            //fill textFields with data X
            // allow edible X
            //validate all fields
            //update all fields
            //??Reload JDialog with updated Fields OR CLOSE??
            //Validate -if bad sql error!
            if(nm.isEmpty() || userName.isEmpty() || 
                admin.isEmpty() || passwd.isEmpty()){
                this.errmsg = "Field can not be empty";
                return;
            }else if(!userName.equals(cuname)){
                String sqlUnm = "SELECT UserName "
                              + "FROM Account";
                ResultSet rs = s.executeQuery(sqlUnm);
                while(rs.next()){
                    if(userName.equalsIgnoreCase(rs.getString("UserName"))){
                        this.errmsg = "UserName: "+userName+" has been taken.";
                        return;
                    }
                }
            }
            this.nm = nm;
            this.username = userName;
            this.admin = admin;
            this.pwd = passwd;
            //update Account
            String sql = "UPDATE Account"+
                        " SET AcctName = '" +this.nm+ 
                        "', UserName = '" +this.username+ 
                        "', AccountType = '" +this.dtype+ 
                        "', Admin = '" +this.admin+ 
                        "', Password = '" +this.pwd+ 
                        "' WHERE AccountNumber = "+this.acctno;
            s.execute(sql);
            
            //update this.dtype+this.userName Account
            String sqlUpdate = "UPDATE "+this.dtype+cuname+
                                " SET AccountName = '" +this.nm+ 
                                "', Type = '" +this.dtype+ 
                                "', Admin = '" +this.admin+ 
                                "' WHERE AccountNumber = "+this.acctno;
                    s.execute(sqlUpdate);
                   if(!userName.equals(cuname)){
                       //update table name for userName Account
                       String sqlUpdateTable = "ALTER TABLE "+this.dtype+cuname+" "
                                             + "RENAME TO "+this.dtype+this.username;
                       s.execute(sqlUpdateTable);
                   }
                   this.actmsg = "Account " + this.acctno + " Updated";
        }catch(SQLException e){
            this.errmsg = "Error Updating Account " + e.getMessage();
        }catch (NumberFormatException e){
            this.errmsg = "NumberFormatException caught: " + e.getMessage();
        }
    }//end of setUpdate

    // account type code (SV,MM,CK)
    @Override
    abstract public String getTypeCd(); 
    //account type description('Saving Account', 'Money Merket', 'Checking Account')
    @Override
    abstract public String getTypeDesc(); 
    
    //GETTERS & SETTER
    @Override
    public int getAcctNo() {
        return acctno;
    }    
    public void setErrMsg(String errmsg) {
        this.errmsg = errmsg;
    }
    public void setActionMsg(String actmsg) {
        this.actmsg = actmsg;
    }
    @Override
    public String getName() {
        return this.nm;
    }
    @Override
    public double getBalance() {
        return this.balance;
    }
    
    public int getAcctID() {
        return this.acctid;
    }
    @Override
    public String getErrMsg() {
        return this.errmsg;
    }
    @Override
    public String getActionMsg() {
        return this.actmsg;
    }
    //@Override
    @Override
    public String getUserName(){
        return this.username;
    }
    
    @Override
    public String getAdmin(){
        return this.admin;
    }
    
    @Override
    public String getPwd(){
        return this.pwd;
    }
    //public ArrayList<String> getLog();//retrieves account lot to display in database
    
}//end of class
