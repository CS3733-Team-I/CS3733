package database.connection;



import database.objects.Edge;
import database.objects.Node;
import database.template.SQLStrings;
import database.objects.InterpreterRequest;
import utility.Node.NodeBuilding;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import utility.Request.Language;
import utility.Request.RequestProgressStatus;

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
        PreparedStatement pstmt = conn.prepareStatement(NODE_INSERT);
        pstmt.setString(1, node.getNodeID());
        pstmt.setInt(2, node.getXcoord());
        pstmt.setInt(3, node.getYcoord());
        pstmt.setInt(4, node.getFloor().ordinal());
        pstmt.setInt(5, node.getBuilding().ordinal());
        pstmt.setInt(6, node.getNodeType().ordinal());
        pstmt.setString(7, node.getLongName());
        pstmt.setString(8, node.getShortName());
        pstmt.setString(9, node.getTeamAssigned());
        return pstmt.executeUpdate();
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

    public static Node selectNode(Connection conn, String nodeID) throws SQLException {
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
            nodes.add(new Node(rs.getString("nodeID"),
                               rs.getInt("xcoord"),
                               rs.getInt("ycoord"),
                               NodeFloor.values()[rs.getInt("floor")],
                               NodeBuilding.values()[rs.getInt("building")],
                               NodeType.values()[rs.getInt("nodeType")],
                               rs.getString("longName"),
                               rs.getString("shortName"),
                               rs.getString("teamAssigned")));
        }
        return nodes;
    }


    public static int selectCountNodeType(Connection conn, NodeType nodeType, NodeFloor floor, String teamAssigned) throws SQLException{
        int result = 0;
        PreparedStatement pstmt = conn.prepareStatement(SQLStrings.NODE_COUNT_NODETYPE);
        pstmt.setInt(1, nodeType.ordinal());
        pstmt.setInt(2, floor.ordinal());
        pstmt.setString(3, teamAssigned);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            result = rs.getInt("countNode");
        }
        return result;
    }

    /**TODO: make request database access as generic as possible to reduce workload**/
    public static int insertInterpreter(Connection conn, InterpreterRequest iR) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(INTERPRETER_INSERT);
        pstmt.setString(1, iR.getRequestID());
        pstmt.setString(2, iR.getNodeID());
        pstmt.setString(3, iR.getAssigner());
        pstmt.setString(4, iR.getNote());
        pstmt.setTimestamp(5, iR.getSubmittedTime());
        pstmt.setTimestamp(6, iR.getCompletedTime());
        pstmt.setInt(7, iR.getStatus().ordinal());
        pstmt.setInt(8, iR.getLanguage().ordinal());
        return pstmt.executeUpdate();
    }

    public static int updateInterpreter(Connection conn, InterpreterRequest iR) throws SQLException {
        String sql = INTERPRETER_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, iR.getNodeID());
        pstmt.setString(2, iR.getAssigner());
        pstmt.setString(3, iR.getNote());
        pstmt.setTimestamp(4, iR.getSubmittedTime());
        pstmt.setTimestamp(5, iR.getCompletedTime());
        pstmt.setInt(6, iR.getStatus().ordinal());
        pstmt.setInt(7, iR.getLanguage().ordinal());
        //search parameter below
        pstmt.setString(8, iR.getRequestID());
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
                rs.getString("assigner"),
                rs.getString("note"),
                rs.getTimestamp("submittedTime"),
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
                    rs.getString("assigner"),
                    rs.getString("note"),
                    rs.getTimestamp("submittedTime"),
                    rs.getTimestamp("completedTime"),
                    RequestProgressStatus.values()[rs.getInt("status")],
                    Language.values()[rs.getInt("language")]);

            interpreterRequests.add(interpreterRequest);
        }
        return interpreterRequests;
    }

}
