package database.template;

public class SQLStrings {

    public static final String CREATE_NODE_TABLE = "create table t_nodes(" +
            "  NodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_pk PRIMARY KEY," +
            "  xcoord int NOT NULL," +
            "  ycoord int NOT NULL," +
            "  floor int NOT NULL," +
            "  building int NOT NULL," +
            "  nodeType int NOT NULL," +
            "  longName VARCHAR(255) NOT NULL," +
            "  shortName VARCHAR(255) NOT NULL," +
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
            " assigner VARCHAR(63) NOT NULL REFERENCES t_employee on DELETE CASCADE," +
            " completer VARCHAR(63) NOT NULL,"+
            " note CLOB(280)," +
            " submittedTime TIMESTAMP NOT NULL," +
            " startedTime TIMESTAMP NOT NULL,"+
            " completedTime TIMESTAMP NOT NULL," +
            " status INT NOT NULL)";

    public static final String REQUEST_INSERT = " ?, ?, ?, ?, ?, ?, ?)";
    public static final String REQUEST_UPDATE =
            " nodeID=?," +
            " assigner=?," +
            " completer=?,"+
            " note=?,"+
            " submittedTime=?,"+
            " startedTime=?,"+
            " completedTime=?,"+
            " status=?"+
            " where requestID=?";

    public static final String CREATE_INTERPRETER_TABLE = "create table t_interpreters("+
            " requestID VARCHAR(36) NOT NULL CONSTRAINT t_interpreters_pk PRIMARY KEY,"+
            " language INT NOT NULL,"+
            " nodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk2" +
            " REFERENCES t_nodes ON DELETE CASCADE,";

    public static final String INTERPRETER_INSERT = "insert into t_interpreters values(?, ?, ?,";
    public static final String INTERPRETER_UPDATE = "update t_interpreters set" +
            " language=?,";
    public static final String INTERPRETER_SELECT = "select * from t_interpreters where requestID=?";
    public static final String INTERPRETER_DELETE = "DELETE FROM t_interpreters WHERE requestID = ?";
    public static final String INTERPRETER_SELECT_ALL = "select * from t_interpreters";

    public static final String CREATE_SECURITY_TABLE = "create table t_security("+
            " requestID VARCHAR(36) NOT NULL CONSTRAINT t_security_pk PRIMARY KEY,"+
            " priority INT NOT NULL,"+
            " nodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk3" +
            " REFERENCES t_nodes ON DELETE CASCADE,";

    public static final String SECURITY_INSERT = "insert into t_security values(?, ?, ?,";
    public static final String SECURITY_UPDATE = "update t_security set" +
            " priority=?,";
    public static final String SECURITY_SELECT = "select * from t_security where requestID=?";
    public static final String SECURITY_DELETE = "DELETE FROM t_security WHERE requestID = ?";
    public static final String SECURITY_SELECT_ALL = "select * from t_security";

    public static final String DROP_NODE_TABLE = "DROP TABLE t_nodes";
    public static final String DROP_EDGE_TABLE = "DROP TABLE t_edges";
    public static final String DROP_INTERPRETER_TABLE = "DROP TABLE t_interpreters";
    public static final String DROP_SECURITY_TABLE = "DROP TABLE t_security";

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
    public static final String NODE_NODETYPE_SELECT = "Select nodeID from T_nodes where xcoord=? and ycoord=? and floor=? and nodeType=?";


    public static final String CREATE_EMPLOYEE_TABLE = "create table t_employee("+
            " loginID INT NOT NULL CONSTRAINT t_employee_pk PRIMARY KEY GENERATED ALWAYS AS IDENTITY" +
            " (START WITH 1, INCREMENT BY 1),"+
            " userName Varchar(50) NOT NULL,"+
            // not sure how I want to store passwords
            " password Varchar(60) NOT NULL,"+
            " permission INT NOT NULL,"+
            " serviceAbility INT NOT NULL"+
            ")";

    public static final String DROP_EMPLOYEE_TABLE = "drop table t_employee";

    public static final String EMPLOYEE_INSERT = "insert into t_employee values(?,?,?,?,?)";
    public static final String EMPLOYEE_UPDATE = "update t_employee set userName=?, password=?, permission=?, serviceAbility=? where loginID=?";
    public static final String EMPLOYEE_SELECT = "select * from t_employee where loginID=?";
    public static final String EMPLOYEE_SELECT_ALL = "select * from t_employee";
    public static final String EMPLOYEE_DELETE = "delete from t_employee where loginID=?";
}
