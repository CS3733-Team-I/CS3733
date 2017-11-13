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
            "  edgeID VARCHAR(21) NOT NULL PRIMARY KEY," +
            "  startNode VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES t_nodes(NodeID)," +
            "  endNode VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES t_nodes(NodeID)" +
            ")";

    public static final String CREATE_INTERPRETERS_TABLE = "CREATE TABLE t_interpreters(" +
            " interpreterID int PRIMARY KEY NOT NULL," +
            " language VARCHAR(20) NOT NULL," +
            " requestID int NOT NULL FOREIGN KEY REFERENCES t_requests(requestID)" +
            ")";


    public static final String CREATE_REQUESTS_TABLE = "CREATE TABLE t_requests(" +
            " requestID int PRIMARY KEY NOT NULL," +
            " locationNode VARCHAR(10) FOREIGN KEY REFERENCES t_nodes(NodeID)" +
            ")";



    public static final String EDGE_INSERT = "insert into T_EDGES values(?, ?, ?)";
    public static final String EDGE_UPDATE = "update T_EDGES set startNode=?, endNode=? where edgeID=?";
    public static final String EDGE_SELECT = "SELECT ? FROM T_EDGES";
    public static final String EDGE_DELETE = "DELETE FROM T_EDGES where edgeID = ?";
    public static final String EDGE_SELECT_ALL = "select * from T_EDGES";

    public static final String NODE_INSERT = "insert into T_NODES values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String NODE_UPDATE = "update T_EDGES set xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=?, teamAssigned=? where edgeID=?";
    public static final String NODE_SELECT = "SELECT ? FROM T_NODES";
    public static final String NODE_DELETE = "DELETE FROM T_NODES WHERE nodesID = ?";
    public static final String NODE_SELECT_ALL = "SELECT * FROM T_NODES";


    public static final String REQUEST_INSERT = "insert into t_requests values(?, ?)";
    public static final String REQUEST_UPDATE = "update t_requests set locationNode=? where requestID=?";
    public static final String REQUEST_SELECT = "select ? from t_requests";
    public static final String REQUEST_DELETE = "delete from t_requests where requestID=?";
    public static final String REQUEST_SELECT_ALL = "select * from t_requests";

    public static final String INTERPRETER_INSERT = "insert into t_interpreters values(?, ?, ?)";
    public static final String INTERPRETER_UPDATE = "update t_interpreters set language=?, requestID=? where interpreterID=?";
    public static final String INTERPRETER_SELECT = "select ? from t_interpreters";
    public static final String INTERPRETER_DELETE = "delete from t_interpreters where interpreterID=?";
    public static final String INTERPRETER_SELECT_ALL = "select * from t_interpreters";
}


/*
        String sql = NODE_INSERT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nodeID);
        pstmt.setInt(2, xc);
        pstmt.setInt(3, yc);
        pstmt.setInt(4, fl);
        pstmt.setInt(5, bu);
        pstmt.setInt(6, nt);
        pstmt.setString(7, ln);
        pstmt.setString(8, sn);
        pstmt.setString(9, assigned);
        pstmt.executeUpdate();
 */
