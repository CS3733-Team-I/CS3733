package database.template;

public class SQLStrings {

    public static final String CREATE_NODE_TABLE = "CREATE TABLE t_nodes (" +
            "    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "    nodeID VARCHAR(10) NOT NULL PRIMARY KEY," +
            "    xcoord int NOT NULL," +
            "    ycoord int NOT NULL," +
            "    floor int NOT NULL," +
            "    building int NOT NULL," +
            "    nodeType int NOT NULL," +
            "    longName VARCHAR(255) NOT NULL," +
            "    shortName VARCHAR(255) NOT NULL," +
            "    teamAssigned VARCHAR(6) NOT NULL" +
            ")";

    public static final String CREATE_NODE_UINDEX =  "CREATE UNIQUE INDEX t_nodes_id_uindex ON t_nodes (id)";

    public static final String CREATE_EDGE_TABLE = "CREATE TABLE t_edges (\n" +
            "    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "    edgeID VARCHAR(21) NOT NULL PRIMARY KEY,\n" +
            "    startNode VARCHAR(10) NOT NULL CONSTRAINT t_edges_t_nodes_fk1" +
            "    REFERENCES t_nodes (nodeID) ON DELETE CASCADE,\n" +
            "    endNode VARCHAR(10) NOT NULL CONSTRAINT t_edges_t_nodes_fk2" +
            "    REFERENCES t_nodes (nodeID) ON DELETE CASCADE\n" +
            ")";

    public static final String CREATE_EDGE_UINDEX =  "CREATE UNIQUE INDEX t_edges_id_uindex ON t_edges (id)";

    public static final String REQUEST_INSERT = " ?, ?, ?, ?, ?)";

    public static final String REQUEST_UPDATE = " nodeID=?, assigner=?, completer=?,"+
            " note=?, submittedTime=?, startedTime=?, completedTime=?,"+
            " status=? where requestID=?";

    public static final String CREATE_INTERPRETER_TABLE = "create table t_interpreter("+
            " requestID VARCHAR(36) NOT NULL CONSTRAINT t_interpreter_pk PRIMARY KEY,"+
            " language INT NOT NULL,"+
            " nodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk2 REFERENCES t_nodes ON DELETE CASCADE,"+
            " assigner INT NOT NULL CONSTRAINT t_employee_fk REFERENCES t_employee ON DELETE CASCADE,"+
            " completer INT NOT NULL CONSTRAINT t_employee_fk1 REFERENCES t_employee ON DELETE CASCADE,";

    public static final String CREATE_SECURITY_TABLE = "create table t_security("+
            " requestID VARCHAR(36) NOT NULL CONSTRAINT t_security_pk PRIMARY KEY,"+
            " priority INT NOT NULL,"+
            " nodeID VARCHAR(10) NOT NULL CONSTRAINT t_nodes_fk3 REFERENCES t_nodes ON DELETE CASCADE,"+
            " assigner INT NOT NULL CONSTRAINT t_employee_fk2 REFERENCES t_employee ON DELETE CASCADE,"+
            " completer INT NOT NULL CONSTRAINT t_employee_fk3 REFERENCES t_employee ON DELETE CASCADE,";

    public static final String CREATE_FOOD_TABLE = "create table t_food("+
            " requestID VARCHAR(36) NOT NULL CONSTRAINT t_food_pk PRIMARY KEY,"+
            " destinationID VARCHAR(10) NOT NULL CONSTRAINT t_food_fk_destinationID REFERENCES t_nodes ON DELETE CASCADE,"+
            " deliveryTime TIMESTAMP NOT NULL,"+
            " sourceID VARCHAR(10) NOT NULL CONSTRAINT t_food_fk_sourceID REFERENCES t_nodes ON DELETE CASCADE,"+
            " assigner INT NOT NULL CONSTRAINT t_food_employee_fk1 REFERENCES t_employee ON DELETE CASCADE,"+
            " completer INT NOT NULL CONSTRAINT t_food_employee_fk2 REFERENCES t_employee ON DELETE CASCADE,";

    public static final String WITH_SHARED_REQUEST_ATTRIBUTES =
            " note LONG VARCHAR," +
            " submittedTime TIMESTAMP NOT NULL," +
            " startedTime TIMESTAMP NOT NULL,"+
            " completedTime TIMESTAMP NOT NULL," +
            " status INT NOT NULL)";

    // Interpreter
    public static final String INTERPRETER_INSERT = "insert into t_interpreter values(?, ?, ?, ?, ?,";
    public static final String INTERPRETER_UPDATE = "update t_interpreter set language=?,";
    public static final String INTERPRETER_SELECT = "select * from t_interpreter where requestID=?";
    public static final String INTERPRETER_DELETE = "DELETE FROM t_interpreter WHERE requestID = ?";
    public static final String INTERPRETER_SELECT_ALL = "select * from t_interpreter";

    // Food
    public static final String FOOD_INSERT = "insert into t_food values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String FOOD_UPDATE = "update t_food set destinationID=?, deliveryTime=?, sourceID=?," +
            " assigner=?, completer=?,"+
            " note=?, submittedTime=?, startedTime=?, completedTime=?,"+
            " status=? where requestID=?";
    public static final String FOOD_SELECT = "select * from t_food where requestID=?";
    public static final String FOOD_DELETE = "DELETE FROM t_food WHERE requestID = ?";
    public static final String FOOD_SELECT_ALL = "select * from t_food";

    // Security
    public static final String SECURITY_INSERT = "insert into t_security values(?, ?, ?, ?, ?,";
    public static final String SECURITY_UPDATE = "update t_security set priority=?,";
    public static final String SECURITY_SELECT = "select * from t_security where requestID=?";
    public static final String SECURITY_DELETE = "DELETE FROM t_security WHERE requestID = ?";
    public static final String SECURITY_SELECT_ALL = "select * from t_security";

    // Dropping
    public static final String DROP_NODE_TABLE = "DROP TABLE t_nodes";
    public static final String DROP_EDGE_TABLE = "DROP TABLE t_edges";
    public static final String DROP_INTERPRETER_TABLE = "DROP TABLE t_interpreter";
    public static final String DROP_SECURITY_TABLE = "DROP TABLE t_security";

    public static final String CREATE_SCHEMA = "CREATE SCHEMA LOCALKIOSK";

    public static final String EDGE_INSERT = "insert into t_edges (edgeID, startNode, endNode) values(?, ?, ?)";
    public static final String EDGE_UPDATE = "update t_edges set startNode=?, endNode=? where edgeID=?";
    public static final String EDGE_SELECT = "SELECT * FROM t_edges where edgeID=?";
    public static final String EDGE_DELETE = "DELETE FROM t_edges where edgeID = ?";
    public static final String EDGE_SELECT_ALL = "select * from t_edges";

    public static final String NODE_INSERT = "insert into t_nodes (nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String NODE_UPDATE = "update t_nodes set xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=?, teamAssigned=? where nodeID=?";
    public static final String NODE_UPDATE_WITHID = "update t_nodes set nodeID=?, xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=?, teamAssigned=? where id=?";
    public static final String NODE_SELECT = "SELECT * FROM t_nodes where nodeID=?";
    public static final String NODE_DELETE = "DELETE FROM t_nodes WHERE nodeID = ?";
    public static final String NODE_SELECT_ALL = "SELECT * FROM T_NODES";
    public static final String NODE_COUNT_NODETYPE = "SELECT COUNT(*) As countNode from T_NODES where nodeType=? and floor=? and teamAssigned=?";
    public static final String NODE_NODETYPE_SELECT = "Select nodeID from T_nodes where xcoord=? and ycoord=? and floor=? and nodeType=?";

    public static final String CREATE_EMPLOYEE_TABLE = "create table t_employee("+
            " id INT GENERATED ALWAYS AS IDENTITY CONSTRAINT t_employee_pk PRIMARY KEY,"+
            " username Varchar(255) NOT NULL UNIQUE,"+
            " lastName Varchar(35) NOT NULL,"+
            " firstName Varchar(35) NOT NULL,"+
            " password Varchar(60) NOT NULL,"+
            " options LONG VARCHAR,"+
            " permission INT NOT NULL,"+
            " serviceAbility INT NOT NULL"+
            ")";

    public static final String DROP_EMPLOYEE_TABLE = "drop table t_employee";

    public static final String EMPLOYEE_INSERT = "insert into t_employee"+
            "(username, lastName, firstName, password, options, permission, serviceAbility)"+
            " values(?, ?, ?, ?, ?, ?, ?)";
    public static final String EMPLOYEE_UPDATE = "update t_employee set username=?, lastName=?, firstName=?, password=?, options=?,"+
            " permission=?, serviceAbility=? where id=?";
    public static final String EMPLOYEE_SELECT = "select * from t_employee where id=?";
    public static final String EMPLOYEE_SELECT_ALL = "select * from t_employee";
    public static final String EMPLOYEE_DELETE = "delete from t_employee where id=?";
}
