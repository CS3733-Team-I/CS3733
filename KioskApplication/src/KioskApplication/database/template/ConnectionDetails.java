package KioskApplication.database.template;

public class ConnectionDetails {

    public static final String DBUSERNAME = "localkiosk";
    private static final String DBPASSWORD = "thisisapassword";
    public static final String DBURL = "jdbc:derby://localhost:1527/kioskapplication;create=true";
    public static final String DBTESTURL = "jdbc:derby://localhost:1527/kiosktests;create=true";
    public static final String DERBYEMBEDDED = "org.apache.derby.jdbc.ClientDriver";

    public static String getPassword() {
        return DBPASSWORD;
    }
}
