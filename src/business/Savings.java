package business;

/**
 *
 * @author Ben Hampton
 */
public class Savings extends AssetAccounts {
    public static final String TYPECD = "SV";
    public static final String TYPEDESC = "Saving";
    
    public Savings(String nm, double startbal, String psswd, String dtype){
        super(nm, startbal, psswd, Savings.TYPECD);
    }
    
    public Savings(String acctno){
        super(acctno, Savings.TYPEDESC);
    }
    
    public Savings(int acctid, String nm, double startval, String passwd, String dtype){
        super(acctid, nm, startval, passwd, Savings.TYPECD);
    }
    
    @Override
    public String getTypeCd(){
        return Savings.TYPECD;
    }
    
    @Override
    public String getTypeDesc(){
        return Savings.TYPEDESC;
    }
    
}
