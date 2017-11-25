package database.template;

public class SQLStrings {

    public static final String CREATE_NODE_TABLE = "create table t_nodes(" +
            "  NodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_pk PRIMARY KEY," +
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
            " edgeID VARCHAR(21) NOT NULL CONSTRAINT t_edges_pk PRIMARY KEY," +
            " startNode VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk" +
            " REFERENCES t_nodes ON DELETE CASCADE," +
            " endNode VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk1" +
            " REFERENCES t_nodes ON DELETE CASCADE" +
            ")";

    public static final String WITH_SHARED_REQUEST_ATTRIBUTES =
                    "( requestID VARCHAR(36) NOT NULL CONSTRAINT t_interpreters_pk PRIMARY KEY," +
                    " nodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk2" +
                    " REFERENCES t_nodes ON DELETE CASCADE," +
                    " assigner VARCHAR(50) NOT NULL," +
                    " note CLOB(280)," +
                    " submittedTime TIMESTAMP NOT NULL," +
                    " completedTime TIMESTAMP NOT NULL," +
                    " status INT NOT NULL";

    public static final String CREATE_INTERPRETER_TABLE = "create table t_interpreters";

    public static final String WITH_INTERPRETER_ATTRIBUTES = ", language INT NOT NULL)";

    public static final String YCREATE_INTERPRETERS_TABLE = "create table t_interpreters(" +
            //Base attributes
            " requestID VARCHAR(36) NOT NULL CONSTRAINT t_interpreters_pk PRIMARY KEY," +
            " nodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk2" +
            " REFERENCES t_nodes ON DELETE CASCADE," +
            " assigner VARCHAR(50) NOT NULL," +
            " note CLOB(256)," +
            " submittedTime TIMESTAMP NOT NULL," +
            " completedTime TIMESTAMP NOT NULL," +
            " status INT NOT NULL," +
            //Unique request attributes
            " language INT NOT NULL" +
            ")";


    public static final String DROP_NODE_TABLE = "DROP TABLE t_nodes";
    public static final String DROP_EDGE_TABLE = "DROP TABLE t_edges";
    public static final String DROP_REQUEST_TABLE = "DROP TABLE t_requests";
    public static final String DROP_INTERPRETER_TABLE = "DROP TABLE t_interpreters";

    public static final String CREATE_SCHEMA = "CREATE SCHEMA LOCALKIOSK";

    public static final String EDGE_INSERT = "insert into t_edges values(?, ?, ?)";
    public static final String EDGE_UPDATE = "update t_edges set startNode=?, endNode=? where edgeID=?";
    public static final String EDGE_SELECT = "SELECT * FROM t_edges where edgeID=?";
    public static final String EDGE_DELETE = "DELETE FROM t_edges where edgeID = ?";
    public static final String EDGE_SELECT_ALL = "select * from t_edges";

    public static final String NODE_INSERT = "insert into t_nodes values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String NODE_UPDATE = "update t_nodes set xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=?, teamAssigned=? where nodeID=?";
    public static final String NODE_SELECT = "SELECT * FROM t_nodes where nodeID=?";
    public static final String NODE_DELETE = "DELETE FROM t_nodes WHERE nodeID = ?";
    public static final String NODE_SELECT_ALL = "SELECT * FROM T_NODES";
    public static final String NODE_COUNT_NODETYPE = "SELECT COUNT(*) As countNode from T_NODES where nodeType=? and floor=? and teamAssigned=?";

    public static final String INTERPRETER_INSERT = "insert into t_interpreters values(?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INTERPRETER_UPDATE = "update t_interpreters set" +
            " nodeID=?," +
            " assigner=?," +
            " note=?,"+
            " submittedTime=?,"+
            " completedTime=?,"+
            " status=?,"+
            " language=?"+
            " where requestID=?";
    public static final String INTERPRETER_SELECT = "select * from t_interpreters where requestID=?";
    public static final String INTERPRETER_DELETE = "DELETE FROM t_interpreters WHERE requestID = ?";
    public static final String INTERPRETER_SELECT_ALL = "select * from t_interpreters";

    public static final String CREATE_EMPLOYEE_TABLE = "create table t_employee("+
            " loginID Varchar(70) NOT NULL CONSTRAINT t_employee_pk PRIMARY KEY,"+
            " loginName Varchar(70) NOT NULL,"+
            // not sure how I want to store passwords
            " password Varchar(70) NOT NULL,"+
            " permission INT NOT NULL,"+
            " serviceAbility INT NOT NULL"+
            ")";

    public static final String DROP_EMPLOYEE_TABLE = "drop table t_employee";

    public static final String EMPLOYEE_INSERT = "insert into t_employee values(?,?,?,?)";
    // this seems insecure AF
    public static final String EMPLOYEE_UPDATE = "update t_employee set loginName=?, password=?, permission=?, serviceAbility=?, where loginID=?";
    // Included a get method for standardization purposes
    public static final String EMPLOYEE_SELECT = "select * from t_employee where loginID=?";
    public static final String EMPLOYEE_SELECT_ALL = "select * from t_employee";
    public static final String EMPLOYEE_DELETE = "delete from t_employee where loginID=?";
}
