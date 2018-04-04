package business;

/**
 *
 * @author Ben Hampton
 */
public class MoneyMarket extends AssetAccounts {
    public static final String TYPECD = "MM";
    public static final String TYPEDESC = "Money Market";
    
    public MoneyMarket(String nm, double startbal, String psswd, String dtype){
        super(nm, startbal, psswd, MoneyMarket.TYPECD);
    }
    
    public MoneyMarket(String acctno){
        super(acctno, MoneyMarket.TYPEDESC);
    }
    
    public MoneyMarket(int acctid, String nm, double startval, String passwd, String dtype){
        super(acctid, nm, startval, passwd, MoneyMarket.TYPECD);
    }
    
    @Override
    public String getTypeCd(){
        return MoneyMarket.TYPECD;
    }
    
    @Override
    public String getTypeDesc(){
        return MoneyMarket.TYPEDESC;
    }
}
