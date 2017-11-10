package database.template;

public class SQLStrings {

    public static final String CREATE_NODE_TABLE = "create table t_nodes(\n" +
            "  NodeID VARCHAR(10)PRIMARY KEY NOT NULL ,\n" +
            "  xcoord int NOT NULL,\n" +
            "  ycoord int NOT NULL,\n" +
            "  floor VARCHAR(2) NOT NULL,\n" +
            "  building VARCHAR(10) NOT NULL,\n" +
            "  nodeType VARCHAR(4) NOT NULL,\n" +
            "  longName VARCHAR(100) NOT NULL,\n" +
            "  shortName VARCHAR(25) NOT NULL,\n" +
            "  teamAssigned VARCHAR(6) NOT NULL\n" +
            ")";

    public static final String CREATE_EDGE_TABLE = "create table t_edges(\n" +
            "  edgeID VARCHAR(21)PRIMARY KEY NOT NULL,\n" +
            "  startNode VARCHAR(10) NOT NULL,\n" +
            "  endNode VARCHAR(10) NOT NULL\n" +
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
}
