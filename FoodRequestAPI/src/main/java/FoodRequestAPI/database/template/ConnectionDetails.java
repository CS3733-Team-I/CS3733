package FoodRequestAPI.database.template;

public class ConnectionDetails {

    public static final String DBUSERNAME = "localkiosk";
    private static final String DBPASSWORD = "thisisapassword";
  
    public static final String DBURL = "jdbc:derby:directory:softEngDatabase";
    public static final String DBTESTURL = "jdbc:derby:directory:softEngTestDatabase";
    public static final String DBCREATE = ";create=true";
    public static final String DBSHUTDOWN = ";shutdown=true";
    public static final String DERBYEMBEDDED = "org.apache.derby.jdbc.EmbeddedDriver";
    
    public static String getPassword() {
        return DBPASSWORD;
    }
}
