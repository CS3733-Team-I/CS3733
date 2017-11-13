package KioskApplication.database.connection;



import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static KioskApplication.database.template.SQLStrings.*;

public class Connector {


    public static Edge insertEdge(Connection conn, String node1, String node2, String edgeID) throws SQLException {
        String sql = EDGE_INSERT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(2, node1);
        pstmt.setString(3, node2);
        pstmt.setString(1, edgeID);
        pstmt.execute();

        return new Edge(edgeID, node1, node2);

    }

    public static Edge updateEdge(Connection conn, String node1, String node2, String edgeID) throws SQLException {
        String sql = EDGE_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(2, node1);
        pstmt.setString(3, node2);
        pstmt.setString(1, edgeID);
        pstmt.executeUpdate();

        return new Edge(edgeID, node1, node2);
    }

    public static void updateEdge(Connection conn, Edge edge) throws SQLException {
        updateEdge(conn, edge.getNode1ID(), edge.getNode2ID(), edge.getEdgeID());
    }

    public static Edge selectEdge(Connection conn, String edgeID) throws SQLException {
        Edge edge = null;
        String sql = EDGE_SELECT; //change T_EDGES
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, edgeID);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
   //         edge = EdgeCollection.getInstance().getEdge(rs.getString("edgeID"));
            edge = new Edge(edgeID, rs.getString("startNode"), rs.getString("endNode"));
        } else {
           //will throw exception
        }
        return edge;
    }

    public static ArrayList<Edge> selectAllEdges(Connection conn) throws SQLException{
        ArrayList<Edge> edges = new ArrayList<>();
        String sql = EDGE_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            edges.add(new Edge(rs.getString("edgeID"), rs.getString("startNode"), rs.getString("endNode")));
        }
        return edges;
    }

    public static void deleteEdge(Connection conn, String edge) throws SQLException {
        String sql = EDGE_DELETE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, edge);
        pstmt.execute();
    }

    public static Node insertNode(Connection conn, int xc, int yc, int fl, int bu, int nt, String ln, String sn
            , String assigned, String nodeID)throws SQLException{
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

        return new Node(nodeID, xc, yc, fl, bu, nt, ln, sn, assigned);
    }

    public static Node updateNode(Connection conn, int xc, int yc, int fl, int bu, int nt, String ln, String sn
            , String assigned, String nodeID)throws SQLException{
        String sql = NODE_UPDATE;
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

        return new Node(nodeID, xc, yc, fl, bu, nt, ln, sn, assigned);
    }

    public static void updateNode(Connection conn, Node node) throws SQLException {
        updateNode(conn, node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding(), node.getNodeType(),
                node.getLongName(), node.getShortName(), node.getTeamAssigned(), node.getNodeID());
    }

    public static Node selectNode(Connection conn, String nodeID) throws SQLException {
        Node node = null;
        String sql = NODE_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nodeID);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            node = new Node(nodeID, rs.getInt("xcoord"), rs.getInt("ycoord"),
                    rs.getInt("floor"), rs.getInt("building"),
                    rs.getInt("nodeType"), rs.getString("longName"),
                    rs.getString("shortName"), rs.getString("teamAssigned"));
        } else {
            //throws exception
        }
        return node;
    }

    public static void deleteNode(Connection conn, String node) throws SQLException {
        String sql = NODE_DELETE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,node);
        pstmt.execute();
    }

    public static ArrayList<Node> selectAllNodes(Connection conn) throws SQLException{
        ArrayList<Node> nodes = new ArrayList<>();
        String sql = NODE_SELECT_ALL;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            nodes.add(new Node(rs.getString("nodeID"), rs.getInt("xcoord"),
                    rs.getInt("ycoord"), rs.getInt("floor"), rs.getInt("building"),
                    rs.getInt("nodeType"), rs.getString("longName"), rs.getString("shortName"),
                    rs.getString("teamAssigned")));
        }
        return nodes;
    }
}
