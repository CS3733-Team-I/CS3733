package KioskApplication.database.connection;



import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.database.template.SQLStrings;
import KioskApplication.entity.InterpreterRequest;
import KioskApplication.entity.Request;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import com.sun.org.apache.regexp.internal.RE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static KioskApplication.database.template.SQLStrings.*;

public class Connector {
    private static int executeEdgeStatement(PreparedStatement pstmt, Edge edge) throws SQLException {
        pstmt.setString(1, edge.getEdgeID());
        pstmt.setString(2, edge.getNode1ID());
        pstmt.setString(3, edge.getNode2ID());
        return pstmt.executeUpdate();
    }

    public static int insertEdge(Connection conn, Edge edge) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(EDGE_INSERT);
        return executeEdgeStatement(pstmt, edge);
    }

    public static int updateEdge(Connection conn, Edge edge) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(EDGE_UPDATE);
        return executeEdgeStatement(pstmt, edge);
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

    private static int executeNodeStatement(PreparedStatement pstmt, Node node) throws SQLException {
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

    public static int insertNode(Connection conn, Node node) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(NODE_INSERT);
        return executeNodeStatement(pstmt, node);
    }

    public static int updateNode(Connection conn, Node node) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(NODE_UPDATE);
        return executeNodeStatement(pstmt, node);
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




    public static Request insertRequest(Connection conn, int requestID,
                                     String locationNode, String employee) throws SQLException{
        String sql = REQUEST_INSERT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, requestID);
        pstmt.setString(2, locationNode);
        pstmt.setString(3, employee);
        pstmt.executeUpdate();
        return new Request(locationNode, employee, requestID);
    }

    public static int updateRequest(Connection conn, int requestID,
                                     String locationNode, String employee) throws SQLException {
        String sql = REQUEST_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, locationNode);
        pstmt.setInt(2, requestID);
        pstmt.setString(3, employee);
        return pstmt.executeUpdate();
    }

    public static Request selectRequest(Connection conn, int requestID) throws SQLException {
        String sql = REQUEST_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, requestID);
        ResultSet rs = pstmt.executeQuery();
        Request request = null;
        if(rs.next()) {
            request = new Request(rs.getString("locationNode"),
                    rs.getString("employee"),
                    rs.getInt("requestID"));
        }
        return request;
    }

    public static void deleteRequest(Connection conn, int requestID) throws SQLException {
        String sql = REQUEST_DELETE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, requestID);
        pstmt.executeUpdate();
    }

    public static ArrayList<Request> selectAllRequests(Connection conn)  throws SQLException {
        String sql = REQUEST_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ArrayList<Request> requests = new ArrayList<>();

        ResultSet rs = pstmt.executeQuery();

        while(rs.next()) {
            requests.add(new Request(rs.getString("locationNode"),
                    rs.getString("employee"),
                    rs.getInt("requestID")));
        }
        return requests;
    }

    public static InterpreterRequest insetInterpreter(Connection conn, int interpreterID, String language,
                                        int requestID) throws SQLException {
        String sql = INTERPRETER_INSERT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, interpreterID);
        pstmt.setString(2, language);
        pstmt.setInt(3, requestID);

        pstmt.executeUpdate();

        return new InterpreterRequest(language, interpreterID, requestID);
    }

    public static int updateIntepreter(Connection conn, int interpreterID,
                                        String language, int requestID) throws SQLException {
        String sql = INTERPRETER_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, language);
        pstmt.setInt(2, requestID);
        pstmt.setInt(3, interpreterID);

        return pstmt.executeUpdate();
    }

    public static InterpreterRequest selectInterpreter(Connection conn, int interpreterID) throws SQLException {
        String sql = INTERPRETER_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, interpreterID);
        InterpreterRequest interpreterRequest = null;

        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            interpreterRequest = new InterpreterRequest(rs.getString(2), rs.getInt(1),
            rs.getInt(3));
        }
        return interpreterRequest;
    }

    public static void deleteInterpreter(Connection conn, int interpreterID) throws SQLException {
        String sql = INTERPRETER_DELETE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, interpreterID);
        pstmt.executeUpdate();
    }

    public static ArrayList<InterpreterRequest> selectAllInterpeters(Connection conn) throws SQLException {
        String sql = INTERPRETER_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        ArrayList<InterpreterRequest> interpreterRequests = new ArrayList<>();
        while(rs.next()) {
            interpreterRequests.add(new InterpreterRequest(rs.getString(2),
                    rs.getInt(1), rs.getInt(3)));
        }
        return interpreterRequests;
    }

}
