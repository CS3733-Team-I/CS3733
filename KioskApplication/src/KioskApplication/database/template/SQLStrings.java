package KioskApplication.database.template;

public class SQLStrings {

    public static final String CREATE_NODE_TABLE = "create table t_nodes(" +
            "  NodeID VARCHAR(10)PRIMARY KEY NOT NULL ," +
            "  xcoord int NOT NULL," +
            "  ycoord int NOT NULL," +
            "  floor int NOT NULL," +
            "  building int NOT NULL," +
            "  nodeType int NOT NULL," +
            "  longName VARCHAR(100) NOT NULL," +
            "  shortName VARCHAR(25) NOT NULL," +
            "  teamAssigned VARCHAR(6) NOT NULL" +
            ")";

    public static final String CREATE_EDGE_TABLE = "create table t_edges(" +
            "  edgeID VARCHAR(21)PRIMARY KEY NOT NULL," +
            "  startNode VARCHAR(10) NOT NULL," +
            "  endNode VARCHAR(10) NOT NULL" +
            ")";


    public static final String CREATE_REQUEST_TABLE = "CREATE TABLE T_Requests(" +
            " RequestID VARCHAR(16) PRIMARY KEY NOT NULL," +
            " RequestType int NOT NULL";

    public static final String EDGE_INSERT = "insert into T_EDGES values(?, ?, ?)";
    public static final String EDGE_UPDATE = "update T_EDGES set startNode=?, endNode=? where edgeID=?";
    public static final String EDGE_SELECT = "SELECT ? FROM T_EDGES";
    public static final String EDGE_DELETE = "DELETE FROM T_EDGES where edgeID = ?";
    public static final String EDGE_SELECT_ALL = "select * from T_EDGES";

    public static final String NODE_INSERT = "insert into T_NODES values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String NODE_UPDATE = "update T_EDGES set xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=?, teamAssigned=? where edgeID=?";
    public static final String NODE_SELECT = "SELECT ? FROM T_NODES";
    public static final String NODE_DELETE = "DELETE FROM T_NODES WHERE NODEID = ?";
    public static final String NODE_SELECT_ALL = "SELECT * FROM T_NODES";
}
