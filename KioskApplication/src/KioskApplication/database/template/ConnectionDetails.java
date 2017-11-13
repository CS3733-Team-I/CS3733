package KioskApplication.database.template;

public class ConnectionDetails {

    public static final String DBUSERNAME = "localkiosk";
    private static final String DBPASSWORD = "thisisapassword";
    public static final String DBURL = "jdbc:derby:softEngDatabase;create=true";
    public static final String DERBYEMBEDDED = "org.apache.derby.jdbc.EmbeddedDriver";

    public static String getPassword() {
        return DBPASSWORD;
    }
}
