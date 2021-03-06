package database.connection;

import database.objects.ActivityLog.ActivityLog;
import database.objects.ActivityLog.ActivityType;
import database.objects.Edge;
import database.objects.Node;
import database.objects.requests.*;
import database.template.SQLStrings;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;
import utility.request.ITService;
import utility.request.Language;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;

import static database.template.SQLStrings.*;

public class Connector {
    public static int insertEdge(Connection conn, Edge edge) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(EDGE_INSERT);
        pstmt.setString(1, edge.getEdgeID());
        pstmt.setString(2, edge.getNode1ID());
        pstmt.setString(3, edge.getNode2ID());
        return pstmt.executeUpdate();
    }

    public static int updateEdge(Connection conn, Edge edge) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(EDGE_UPDATE);
        pstmt.setString(1, edge.getNode1ID());
        pstmt.setString(2, edge.getNode2ID());
        pstmt.setString(3, edge.getEdgeID());
        return pstmt.executeUpdate();
    }

    public static Edge selectEdge(Connection conn, String edgeID) throws SQLException {
        Edge edge = null;
        String sql = EDGE_SELECT; //change T_EDGES
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, edgeID);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            edge = new Edge(edgeID, rs.getString("startNode"), rs.getString("endNode"));
        }
        return edge;
    }

    public static ArrayList<Edge> selectAllEdges(Connection conn) throws SQLException{
        ArrayList<Edge> edges = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement(EDGE_SELECT_ALL);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            edges.add(new Edge(rs.getString("edgeID"), rs.getString("startNode"), rs.getString("endNode")));
        }
        return edges;
    }

    public static boolean deleteEdge(Connection conn, Edge edge) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(SQLStrings.EDGE_DELETE);
        pstmt.setString(1, edge.getEdgeID());
        return pstmt.execute();
    }

    public static int insertNode(Connection conn, Node node) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(NODE_INSERT, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, node.getNodeID());
        pstmt.setInt(2, node.getXcoord());
        pstmt.setInt(3, node.getYcoord());
        pstmt.setInt(4, node.getFloor().ordinal());
        pstmt.setInt(5, node.getBuilding().ordinal());
        pstmt.setInt(6, node.getNodeType().ordinal());
        pstmt.setString(7, node.getLongName());
        pstmt.setString(8, node.getShortName());
        pstmt.setString(9, node.getTeamAssigned());
        int result = pstmt.executeUpdate();
        if (result == 1) {
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    public static int updateNode(Connection conn, Node node) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(NODE_UPDATE);

        pstmt.setInt(1, node.getXcoord());
        pstmt.setInt(2, node.getYcoord());
        pstmt.setInt(3, node.getFloor().ordinal());
        pstmt.setInt(4, node.getBuilding().ordinal());
        pstmt.setInt(5, node.getNodeType().ordinal());
        pstmt.setString(6, node.getLongName());
        pstmt.setString(7, node.getShortName());
        pstmt.setString(8, node.getTeamAssigned());
        pstmt.setString(9, node.getNodeID());

        return pstmt.executeUpdate();
    }

    /**
     * Update node with the uniqueID(when a node is update, it's nodeID should be updated too)
     * @param con
     * @param node
     * @return
     * @throws SQLException
     */
    public static int updateNodeWithID(Connection con, Node node) throws SQLException{
        PreparedStatement pstmt = con.prepareStatement(NODE_UPDATE_WITHID);
        pstmt.setString(1, node.getNodeID());
        pstmt.setInt(2, node.getXcoord());
        pstmt.setInt(3, node.getYcoord());
        pstmt.setInt(4, node.getFloor().ordinal());
        pstmt.setInt(5, node.getBuilding().ordinal());
        pstmt.setInt(6, node.getNodeType().ordinal());
        pstmt.setString(7, node.getLongName());
        pstmt.setString(8, node.getShortName());
        pstmt.setString(9, node.getTeamAssigned());
        pstmt.setInt(10, node.getUniqueID());

        return pstmt.executeUpdate();
    }

    public static Node selectNode(Connection conn, String nodeID) throws SQLException, NotFoundException {
        Node node = null;
        String sql = NODE_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nodeID);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            node = new Node(nodeID, rs.getInt("xcoord"),
                    rs.getInt("ycoord"),
                    NodeFloor.values()[rs.getInt("floor")],
                    NodeBuilding.values()[rs.getInt("building")],
                    NodeType.values()[rs.getInt("nodeType")],
                    rs.getString("longName"),
                    rs.getString("shortName"),
                    rs.getString("teamAssigned"));
        }
        if(node == null)
            throw new NotFoundException("Node not found.");
        return node;
    }

    public static Node selectNodeByUniqueID(Connection conn, int uniqueID) throws SQLException, NotFoundException{
        Node node = null;
        String sql = NODE_SELECT_UNIQUEID;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uniqueID);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            node = new Node(
                    rs.getString("nodeID"),
                    rs.getInt("xcoord"),
                    rs.getInt("ycoord"),
                    NodeFloor.values()[rs.getInt("floor")],
                    NodeBuilding.values()[rs.getInt("building")],
                    NodeType.values()[rs.getInt("nodeType")],
                    rs.getString("longName"),
                    rs.getString("shortName"),
                    rs.getString("teamAssigned"));
            node.setUniqueID(uniqueID);
        }
        return node;
    }

    public static boolean deleteNode(Connection conn, Node node) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(SQLStrings.NODE_DELETE);
        pstmt.setString(1, node.getNodeID());
        return pstmt.execute();
    }

    public static ArrayList<Node> selectAllNodes(Connection conn) throws SQLException{
        ArrayList<Node> nodes = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement(SQLStrings.NODE_SELECT_ALL);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Node nd = new Node(rs.getString("nodeID"),
                    rs.getInt("xcoord"),
                    rs.getInt("ycoord"),
                    NodeFloor.values()[rs.getInt("floor")],
                    NodeBuilding.values()[rs.getInt("building")],
                    NodeType.values()[rs.getInt("nodeType")],
                    rs.getString("longName"),
                    rs.getString("shortName"),
                    rs.getString("teamAssigned"));
            nd.setUniqueID(rs.getInt("id"));
            nodes.add(nd);
        }
        return nodes;
    }


    // change
    public static String selectCountNodeType(Connection conn, NodeType nodeType, NodeFloor floor, TeamAssigned teamAssigned) throws SQLException{
        String result = "";
        if(nodeType != NodeType.ELEV) {
            PreparedStatement pstmt = conn.prepareStatement(SQLStrings.NODE_COUNT_NODETYPE);
            pstmt.setInt(1, nodeType.ordinal());
            pstmt.setInt(2, floor.ordinal());
            pstmt.setString(3, "Team " + teamAssigned.name());
            System.out.println(teamAssigned.name());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result = String.valueOf(rs.getInt("countNode"));
            }
        }else{
            PreparedStatement pstmt = conn.prepareStatement(SQLStrings.NODE_NODETYPE_SELECT);
            pstmt.setInt(1, nodeType.ordinal());
            pstmt.setInt(2, floor.ordinal());
            pstmt.setString(3, "Team " + teamAssigned.name());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                result = result + rs.getString("nodeID").charAt(7);
            }
            System.out.println(result);
        }
        return result;
    }

    public static String selectNodeID(Connection conn, int xcoord, int ycoord, NodeFloor nodeFloor, NodeType nodeType) throws SQLException{
        String result = "";
        PreparedStatement pstmt = conn.prepareStatement(SQLStrings.NODE_NODETYPE_SELECT);
        pstmt.setInt(1, xcoord);
        pstmt.setInt(2, ycoord);
        pstmt.setInt(3, nodeFloor.ordinal());
        pstmt.setInt(4, nodeType.ordinal());
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            result = rs.getString("nodeID");
        }
        return result;
    }


    /**TODO: make request database access as generic as possible to reduce workload**/
    public static int insertInterpreter(Connection conn, InterpreterRequest iR) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(INTERPRETER_INSERT+REQUEST_INSERT);
        pstmt.setString(1, iR.getRequestID());
        pstmt.setInt(2, iR.getLanguage().ordinal());
        pstmt.setString(3, iR.getNodeID());
        pstmt.setInt(4, iR.getAssignerID());
        pstmt.setInt(5, iR.getCompleterID());
        pstmt.setString(6, iR.getNote());
        pstmt.setTimestamp(7, iR.getSubmittedTime());
        pstmt.setTimestamp(8, iR.getStartedTime());
        pstmt.setTimestamp(9, iR.getCompletedTime());
        pstmt.setInt(10, iR.getStatus().ordinal());
        //pstmt.setInt(11, iR.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static int updateInterpreter(Connection conn, InterpreterRequest iR) throws SQLException {
        String sql = INTERPRETER_UPDATE+REQUEST_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, iR.getLanguage().ordinal());
        pstmt.setString(2, iR.getNodeID());
        pstmt.setInt(3, iR.getAssignerID());
        pstmt.setInt(4, iR.getCompleterID());
        pstmt.setString(5, iR.getNote());
        pstmt.setTimestamp(6, iR.getSubmittedTime());
        pstmt.setTimestamp(7, iR.getStartedTime());
        pstmt.setTimestamp(8, iR.getCompletedTime());
        pstmt.setInt(9, iR.getStatus().ordinal());
        //search parameter below
        pstmt.setString(10, iR.getRequestID());
        //pstmt.setInt(11, iR.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static InterpreterRequest selectInterpreter(Connection conn, String requestID) throws SQLException {
        String sql = INTERPRETER_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, requestID);
        InterpreterRequest interpreterRequest = null;

        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            //for completed InterpreterRequests
            interpreterRequest = new InterpreterRequest(
                    requestID,
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    Language.values()[rs.getInt("language")]//,
                    //rs.getInt("uRequestID")
            );
        }
        return interpreterRequest;
    }

    public static boolean deleteInterpreter(Connection conn, String requestID) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(INTERPRETER_DELETE);
        pstmt.setString(1, requestID);
        return pstmt.execute();
    }

    public static LinkedList<InterpreterRequest> selectAllInterpreters(Connection conn) throws SQLException {
        String sql = INTERPRETER_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        LinkedList<InterpreterRequest> interpreterRequests = new LinkedList<>();
        while(rs.next()) {
            InterpreterRequest interpreterRequest = null;
            //for completed InterpreterRequests
            interpreterRequest = new InterpreterRequest(
                    rs.getString("requestID"),
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    Language.values()[rs.getInt("language")]//,
                    //rs.getInt("uRequestID")
            );

            interpreterRequests.add(interpreterRequest);
        }
        return interpreterRequests;
    }

    /**TODO: make request database access as generic as possible to reduce workload**/
    public static int insertSecurity(Connection conn, SecurityRequest sR) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(SECURITY_INSERT+REQUEST_INSERT);
        pstmt.setString(1, sR.getRequestID());
        pstmt.setInt(2, sR.getPriority());
        pstmt.setString(3, sR.getNodeID());
        pstmt.setInt(4, sR.getAssignerID());
        pstmt.setInt(5, sR.getCompleterID());
        pstmt.setString(6, sR.getNote());
        pstmt.setTimestamp(7, sR.getSubmittedTime());
        pstmt.setTimestamp(8, sR.getStartedTime());
        pstmt.setTimestamp(9, sR.getCompletedTime());
        pstmt.setInt(10, sR.getStatus().ordinal());
        //pstmt.setInt(11, sR.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static int updateSecurity(Connection conn, SecurityRequest sR) throws SQLException {
        String sql = SECURITY_UPDATE+REQUEST_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, sR.getPriority());
        pstmt.setString(2, sR.getNodeID());
        pstmt.setInt(3, sR.getAssignerID());
        pstmt.setInt(4, sR.getCompleterID());
        pstmt.setString(5, sR.getNote());
        pstmt.setTimestamp(6, sR.getSubmittedTime());
        pstmt.setTimestamp(7, sR.getStartedTime());
        pstmt.setTimestamp(8, sR.getCompletedTime());
        pstmt.setInt(9, sR.getStatus().ordinal());
        //search parameter below
        pstmt.setString(10, sR.getRequestID());
        //pstmt.setInt(11, sR.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static SecurityRequest selectSecurity(Connection conn, String requestID) throws SQLException {
        String sql = SECURITY_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, requestID);
        SecurityRequest securityRequest = null;

        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            //for completed InterpreterRequests
            securityRequest = new SecurityRequest(
                    requestID,
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    rs.getInt("priority")//,
                    //rs.getInt("uRequestID")
            );
        }
        return securityRequest;
    }

    public static boolean deleteSecurity(Connection conn, String requestID) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(SECURITY_DELETE);
        pstmt.setString(1, requestID);
        return pstmt.execute();
    }

    public static LinkedList<SecurityRequest> selectAllSecurity(Connection conn) throws SQLException {
        String sql = SECURITY_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        LinkedList<SecurityRequest> securityRequests = new LinkedList<>();
        while(rs.next()) {
            SecurityRequest securityRequest = null;
            //for completed SecurityRequests
            securityRequest = new SecurityRequest(
                    rs.getString("requestID"),
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    rs.getInt("priority")//,
                    //rs.getInt("uRequestID")
            );

            securityRequests.add(securityRequest);
        }
        return securityRequests;
    }

    public static int insertFood(Connection conn, FoodRequest fR) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(FOOD_INSERT);
        pstmt.setString(1, fR.getRequestID());
        pstmt.setString(2,fR.getRestaurantID());
        pstmt.setTimestamp(3, fR.getDeliveryDate());
        pstmt.setString(4, fR.getNodeID());
        pstmt.setInt(5, fR.getAssignerID());
        pstmt.setInt(6, fR.getCompleterID());
        pstmt.setString(7, fR.getNote());
        pstmt.setTimestamp(8, fR.getSubmittedTime());
        pstmt.setTimestamp(9, fR.getStartedTime());
        pstmt.setTimestamp(10, fR.getCompletedTime());
        pstmt.setInt(11, fR.getStatus().ordinal());
        //pstmt.setInt(12, fR.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static int updateFood(Connection conn, FoodRequest fR) throws SQLException{
        String sql = FOOD_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,fR.getRestaurantID());
        pstmt.setTimestamp(2, fR.getDeliveryDate());
        pstmt.setString(3, fR.getNodeID());
        pstmt.setInt(4, fR.getAssignerID());
        pstmt.setInt(5, fR.getCompleterID());
        pstmt.setString(6, fR.getNote());
        pstmt.setTimestamp(7, fR.getSubmittedTime());
        pstmt.setTimestamp(8, fR.getStartedTime());
        pstmt.setTimestamp(9, fR.getCompletedTime());
        pstmt.setInt(10, fR.getStatus().ordinal());
        //search parameter below
        pstmt.setString(11, fR.getRequestID());
        //pstmt.setInt(12, fR.getuRequestID());
        return pstmt.executeUpdate();

    }

    public static FoodRequest selectFood(Connection conn, String requestID) throws SQLException{
        String sql = FOOD_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, requestID);
        FoodRequest foodRequest = null;

        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            //for completed FoodRequests
            foodRequest = new FoodRequest(
                    requestID,
                    rs.getString("sourceID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    rs.getString("destinationID"),
                    rs.getTimestamp("deliveryTime")//,
                    //rs.getInt("uRequestID")
            );
        }
        return  foodRequest;
    }

    public static boolean deleteFood(Connection conn, String requestID) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(FOOD_DELETE);
        pstmt.setString(1,requestID);
        return pstmt.execute();
    }

    public static LinkedList<FoodRequest> selectAllFood(Connection conn) throws SQLException {
        String sql = FOOD_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        LinkedList<FoodRequest> foodRequests = new LinkedList<>();
        while(rs.next()){
            FoodRequest foodRequest = new FoodRequest(
                    rs.getString("requestID"),
                    rs.getString("sourceID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    rs.getString("destinationID"),
                    rs.getTimestamp("deliveryTime")//,
                    //rs.getInt("uRequestID")
            );

            foodRequests.add(foodRequest);
        }

        return foodRequests;
    }

    /**
     * for inserting Janitor requests into the database
     * @param conn the connection to the database
     * @param jR the JanitorRequest
     * @return
     * @throws SQLException
     */
    public static int insertJanitor(Connection conn, JanitorRequest jR) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(JANITOR_INSERT);
        pstmt.setString(1, jR.getRequestID());
        pstmt.setString(2, jR.getNodeID());
        pstmt.setInt(3, jR.getAssignerID());
        pstmt.setInt(4, jR.getCompleterID());
        pstmt.setString(5, jR.getNote());
        pstmt.setTimestamp(6, jR.getSubmittedTime());
        pstmt.setTimestamp(7, jR.getStartedTime());
        pstmt.setTimestamp(8, jR.getCompletedTime());
        pstmt.setInt(9, jR.getStatus().ordinal());
        //pstmt.setInt(10, jR.getuRequestID());
        return pstmt.executeUpdate();
    }

    /**
     * For updating a stored JanitorRequest
     * @param conn
     * @param jR the updated request
     * @return
     * @throws SQLException
     */
    public static int updateJanitor(Connection conn, JanitorRequest jR) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(JANITOR_UPDATE+REQUEST_UPDATE);
        pstmt.setString(1, jR.getNodeID());
        pstmt.setInt(2, jR.getAssignerID());
        pstmt.setInt(3, jR.getCompleterID());
        pstmt.setString(4, jR.getNote());
        pstmt.setTimestamp(5, jR.getSubmittedTime());
        pstmt.setTimestamp(6, jR.getStartedTime());
        pstmt.setTimestamp(7, jR.getCompletedTime());
        pstmt.setInt(8, jR.getStatus().ordinal());
        //search parameter below
        pstmt.setString(9, jR.getRequestID());
        //pstmt.setInt(10, jR.getuRequestID());
        return pstmt.executeUpdate();
    }

    /**
     * for retrieving JanitorRequests from the database
     * @param conn
     * @param requestID
     * @return
     * @throws SQLException
     */
    public static JanitorRequest selectJanitor(Connection conn, String requestID) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(JANITOR_SELECT);
        pstmt.setString(1, requestID);
        JanitorRequest janitorRequest = null;
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            janitorRequest = new JanitorRequest(
                    requestID,
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")]
                    //rs.getInt("uRequestID")
            );
        }
        return janitorRequest;
    }

    /**
     * Returns true if the request has been found and deleted
     * @param conn
     * @param requestID
     * @return
     * @throws SQLException
     */
    public static boolean deleteJanitor(Connection conn, String requestID) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(JANITOR_DELETE);
        pstmt.setString(1, requestID);
        return pstmt.execute();
    }

    /**
     * For retrieving all JanitorRequests from the database
     * @param conn
     * @return
     * @throws SQLException
     */
    public static LinkedList<JanitorRequest> selectAllJanitor(Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(JANITOR_SELECT_ALL);
        ResultSet rs = pstmt.executeQuery();
        LinkedList<JanitorRequest> janitorRequests = new LinkedList<>();
        while(rs.next()) {
            JanitorRequest janitorRequest = null;
            janitorRequest = new JanitorRequest(
                    rs.getString("requestID"),
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")]
                    //rs.getInt("uRequestID")
            );
            janitorRequests.add(janitorRequest);
        }
        return janitorRequests;
    }

    public static int insertIT(Connection conn, ITRequest itRequest) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(IT_INSERT);
        pstmt.setString(1, itRequest.getRequestID());
        pstmt.setInt(2,itRequest.getItService().ordinal());
        pstmt.setString(3, itRequest.getNodeID());
        pstmt.setInt(4, itRequest.getAssignerID());
        pstmt.setInt(5, itRequest.getCompleterID());
        pstmt.setString(6, itRequest.getNote());
        pstmt.setTimestamp(7, itRequest.getSubmittedTime());
        pstmt.setTimestamp(8, itRequest.getStartedTime());
        pstmt.setTimestamp(9, itRequest.getCompletedTime());
        pstmt.setInt(10, itRequest.getStatus().ordinal());
        //pstmt.setInt(11, itRequest.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static int updateIT(Connection conn, ITRequest itRequest) throws SQLException {
        String sql = IT_UPDATE+REQUEST_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,itRequest.getItService().ordinal());
        pstmt.setString(2, itRequest.getNodeID());
        pstmt.setInt(3, itRequest.getAssignerID());
        pstmt.setInt(4, itRequest.getCompleterID());
        pstmt.setString(5, itRequest.getNote());
        pstmt.setTimestamp(6, itRequest.getSubmittedTime());
        pstmt.setTimestamp(7, itRequest.getStartedTime());
        pstmt.setTimestamp(8, itRequest.getCompletedTime());
        pstmt.setInt(9, itRequest.getStatus().ordinal());
        //search parameter below
        pstmt.setString(10, itRequest.getRequestID());
        //pstmt.setInt(11, itRequest.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static ITRequest selectIT(Connection conn, String requestID) throws SQLException {
        String sql = IT_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, requestID);
        ITRequest itRequest = null;

        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            itRequest = new ITRequest(
                    requestID,
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    ITService.values()[rs.getInt("itService")]
                    //rs.getInt("uRequestID")
            );
        }
        return itRequest;
    }

    public static boolean deleteIT(Connection conn, String requestID) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(IT_DELETE);
        pstmt.setString(1, requestID);
        return pstmt.execute();
    }

    public static LinkedList<ITRequest> selectAllIT(Connection conn) throws SQLException {
        String sql = IT_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        LinkedList<ITRequest> itRequests = new LinkedList<>();
        while(rs.next()) {
            ITRequest itRequest = null;
            itRequest = new ITRequest(
                    rs.getString("requestID"),
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    ITService.values()[rs.getInt("itService")]
                    //rs.getInt("uRequestID")
            );

            itRequests.add(itRequest);
        }
        return itRequests;
    }


    public static int insertMT(Connection conn, MaintenanceRequest mtRequest) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(MT_INSERT+REQUEST_INSERT);
        pstmt.setString(1, mtRequest.getRequestID());
        pstmt.setInt(2,mtRequest.getPriority());
        pstmt.setString(3, mtRequest.getNodeID());
        pstmt.setInt(4, mtRequest.getAssignerID());
        pstmt.setInt(5, mtRequest.getCompleterID());
        pstmt.setString(6, mtRequest.getNote());
        pstmt.setTimestamp(7, mtRequest.getSubmittedTime());
        pstmt.setTimestamp(8, mtRequest.getStartedTime());
        pstmt.setTimestamp(9, mtRequest.getCompletedTime());
        pstmt.setInt(10, mtRequest.getStatus().ordinal());
        return pstmt.executeUpdate();
    }

    public static int updateMT(Connection conn, MaintenanceRequest mtRequest) throws SQLException {
        String sql = MT_UPDATE+REQUEST_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, mtRequest.getPriority());
        pstmt.setString(2, mtRequest.getNodeID());
        pstmt.setInt(3, mtRequest.getAssignerID());
        pstmt.setInt(4, mtRequest.getCompleterID());
        pstmt.setString(5, mtRequest.getNote());
        pstmt.setTimestamp(6, mtRequest.getSubmittedTime());
        pstmt.setTimestamp(7, mtRequest.getStartedTime());
        pstmt.setTimestamp(8, mtRequest.getCompletedTime());
        pstmt.setInt(9, mtRequest.getStatus().ordinal());
        //search parameter below
        pstmt.setString(10, mtRequest.getRequestID());
        //pstmt.setInt(11, sR.getuRequestID());
        return pstmt.executeUpdate();
    }

    public static MaintenanceRequest selectMT(Connection conn, String requestID) throws SQLException {
        String sql = MT_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, requestID);
        MaintenanceRequest maintenanceRequest = null;

        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            //for completed InterpreterRequests
            maintenanceRequest = new MaintenanceRequest(
                    requestID,
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    rs.getInt("priority")//,
                    //rs.getInt("uRequestID")
            );
        }
        return maintenanceRequest;
    }

    public static boolean deleteMT(Connection conn, String requestID) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(MT_DELETE);
        pstmt.setString(1, requestID);
        return pstmt.execute();
    }

    public static LinkedList<MaintenanceRequest> selectAllMT(Connection conn) throws SQLException {
        String sql = MT_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        LinkedList<MaintenanceRequest> maintenanceRequests = new LinkedList<>();
        while(rs.next()) {
            MaintenanceRequest maintenanceRequest = null;
            //for completed SecurityRequests
            maintenanceRequest = new MaintenanceRequest(
                    rs.getString("requestID"),
                    rs.getString("nodeID"),
                    rs.getInt("assigner"),
                    rs.getInt("completer"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("startedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    rs.getInt("priority")//,
                    //rs.getInt("uRequestID")
            );

            maintenanceRequests.add(maintenanceRequest);
        }
        return maintenanceRequests;
    }

    /*public static int getURequestIDFromRequestID(Connection conn, String requestID) throws SQLException {
        String sql = SELECT_REQUEST_UID;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        String table;
        switch (checkRequestType(requestID)) {
            case FOOD:
                table = "t_food";
                break;
            case INTERPRETER:
                table = "t_interpreter";
                break;
            case SECURITY:
                table = "t_security";
                break;
            case GENERAL:
                table="";
                break;
            case JANITOR:
                table="t_janitor";
                break;

            default:
                table="";
        }
        pstmt.setString(1, table);
        pstmt.setString(2, requestID);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            return rs.getInt("uRequestID");
        }
        return -1;
    }*/

    private static RequestType checkRequestType(String requestID) {
        String requestType = requestID.substring(0, 3);
        if (requestType.equals("Int")) {
            return RequestType.INTERPRETER;
        } else if (requestType.equals("Sec")) {
            return RequestType.SECURITY;
        } else if (requestType.equals("Foo")) {
            return RequestType.FOOD;
        } else if (requestType.equals("Jan")) {
            return RequestType.JANITOR;
        }else if(requestType.equals("Man")) {
            return RequestType.MAINTENANCE;
        }else if(requestType.equals("ITT")){
            return RequestType.IT;
            //} else if (requestType.equals("Ins")) {
            //} else if (requestType.equals("Out")) {
        }
        else {
            System.out.println("Invalid requestID");
            return null;
        }
    }

    /*public static boolean addEmployeeToRequestTable(Connection conn,  String employeeID) throws SQLException {
        String sql = ADD_COLUMN_REQUEST_VIEW;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, employeeID);
        return pstmt.execute();
    }
    public static int addRequestToTable(Connection conn, Request request) throws SQLException {
        String sql = INSERT_REQUEST_VIEW;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, request.getuRequestID());
        return pstmt.executeUpdate();
    }
    public static int addRequestToTable(Connection conn, int uRequestID) throws SQLException {
        String sql = INSERT_REQUEST_VIEW;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uRequestID);
        return pstmt.executeUpdate();
    }
    public static int updateRequestViewTable(Connection conn, int uRequestID, String employeeID, boolean isViewed) throws SQLException {
        String sql = UPDATE_REQUEST_VIEW;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, employeeID);
        pstmt.setBoolean(2, isViewed);
        pstmt.setInt(3, uRequestID);
        return pstmt.executeUpdate();
    }
    public static boolean selectRequestViewEmployee(Connection conn, int uRequestID, String employeeID) throws SQLException {
        String sql = SELECT_REQUEST_VIEW_EMPLOYEE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, employeeID);
        pstmt.setInt(2, uRequestID);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            return rs.getBoolean(1);
        }
        return false;
    }
    public static int deleteRequestFromView(Connection conn, int uRequestID) throws SQLException {
        String sql = DELETE_REQUEST_VIEW;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uRequestID);
        return pstmt.executeUpdate();
    }
    public static int deleteEmployeeFromView(Connection conn, String employeeID) throws SQLException {
        String sql = DELETE_EMPLOYEE_VIEW;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, employeeID);
        return pstmt.executeUpdate();
    }*/

    /**
     * Adds the ActivityLog to the database
     * @param log
     * @return integer value of the autogenerated activityID to key the hashmap
     */
    public static int insertActivityLog(Connection conn, ActivityLog log) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(ACTIVITY_INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
        pstmt.setTimestamp(1,log.getTime());
        pstmt.setInt(2,log.getActivityType().ordinal());
        pstmt.setInt(3,log.getEmployeeID());
        pstmt.setString(4,log.getDetails());
        // returns the autogenerated ID
        int result = pstmt.executeUpdate();
        if (result == 1) {
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    public static boolean deleteActivityLog(Connection conn, ActivityLog log) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(ACTIVITY_DELETE);
        pstmt.setInt(1,log.getActivityID());
        return pstmt.execute();
    }

    public static LinkedList<ActivityLog> selectAllActivityLogs(Connection conn) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(ACTIVITY_SELECT_ALL);
        ResultSet rs = pstmt.executeQuery();
        LinkedList<ActivityLog> logs = new LinkedList<>();
        while(rs.next()){
            ActivityLog log = new ActivityLog(
                    rs.getInt("activityID"),
                    rs.getTimestamp("time"),
                    ActivityType.values()[rs.getInt("changeType")],
                    rs.getInt("employee"),
                    rs.getString("details")
            );
            logs.add(log);
        }
        return logs;
    }
}