package database.connection;

import database.objects.*;
import database.template.SQLStrings;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;
import utility.request.Language;
import utility.request.RequestProgressStatus;

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
                Language.values()[rs.getInt("language")]);
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
                    Language.values()[rs.getInt("language")]);

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
                    rs.getInt("priority"));
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
                    rs.getInt("priority"));

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
                    rs.getTimestamp("deliveryTime")
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
                    rs.getTimestamp("deliveryTime")
            );

            foodRequests.add(foodRequest);
        }

        return foodRequests;
    }
}
