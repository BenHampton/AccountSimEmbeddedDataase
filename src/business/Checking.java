package business;

/**
 *
 * @author Ben Hampton
 */
public class Checking extends AssetAccounts{
    public static final String TYPECD = "CK";
    public static final String TYPEDESC = "Checking";
    
    public Checking(String nm, double startbal, String psswd, String dtype){
        super(nm, startbal, psswd, Checking.TYPECD);
    }
    
    public Checking(String acctno){
        super(acctno, Checking.TYPECD);
    }
    
    public Checking(int acctid, String nm, double startval, String passwd, String dtype){
        super(acctid, nm, startval, passwd, Checking.TYPECD);
    }
    
    @Override
    public String getTypeCd(){
        return Checking.TYPECD;
    }
    
    @Override
    public String getTypeDesc(){
        return Checking.TYPEDESC;
    }
    
    @Override
    public void setInterest(double ir){
        String msg = "Interest Request: Checking Accounts do NOT earn interest";
        super.setErrMsg(msg);
    }
}
