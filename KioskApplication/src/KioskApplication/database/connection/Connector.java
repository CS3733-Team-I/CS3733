package KioskApplication.database.connection;



import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.EdgeCollection;
import KioskApplication.database.objects.Node;
import KioskApplication.database.objects.NodeCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static database.template.SQLStrings.*;

public class Connector {


    public static void insertEdge(Connection conn, Edge edge) throws SQLException {
        String sql = EDGE_INSERT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, edge.getEdgeID());
        pstmt.setString(2, edge.getNode1().getNodeID());
        pstmt.setString(3, edge.getNode2().getNodeID());
        pstmt.execute();
    }

    public static void updateEdge(Connection conn, Edge edge) throws SQLException {
        String sql = EDGE_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, edge.getNode1().getNodeID());
        pstmt.setString(2, edge.getNode2().getNodeID());
        pstmt.setString(3, edge.getEdgeID());
        pstmt.executeUpdate();
    }

    public static Edge selectEdge(Connection conn, String edgeID) throws SQLException {
        Edge edge = null;
        String sql = EDGE_SELECT; //change T_EDGES
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, edgeID);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            edge = EdgeCollection.getInstance().getEdge(rs.getString("edgeID"));
            edge.setNode1(rs.getString("startNode"));
            edge.setNode2(rs.getString("endNode"));
        } else {

        }
        return edge;
    }

    public void deleteEdge(Connection conn, Edge edge) throws SQLException {
        String sql = EDGE_DELETE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, edge.getEdgeID());
        pstmt.execute();
        EdgeCollection.getInstance().deleteEdge(edge.getEdgeID());
    }

    public static void insertNode(Connection conn, Node node)throws SQLException{
        String sql = NODE_INSERT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, node.getNodeID());
        pstmt.setInt(2, node.getXcoord());
        pstmt.setInt(3, node.getYcoord());
        pstmt.setString(4, node.getFloor());
        pstmt.setString(5, node.getBuilding());
        pstmt.setString(6, node.getNodeType());
        pstmt.setString(7, node.getLongName());
        pstmt.setString(8, node.getShortName());
        pstmt.setString(9, node.getTeamAssigned());
        pstmt.execute();
    }

    public void updateNode(Connection conn, Node node)throws SQLException{
        String sql = NODE_UPDATE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, node.getXcoord());
        pstmt.setInt(2, node.getYcoord());
        pstmt.setString(3, node.getFloor());
        pstmt.setString(4, node.getBuilding());
        pstmt.setString(5, node.getNodeType());
        pstmt.setString(6, node.getLongName());
        pstmt.setString(7, node.getShortName());
        pstmt.setString(8, node.getTeamAssigned());
        pstmt.executeUpdate();
    }

    public Node selectNodes(Connection conn, String nodeID) throws SQLException {
        Node node = null;
        String sql = NODE_SELECT;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nodeID);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            node = NodeCollection.getInstance().getNode(nodeID);
            node.setXcoord(rs.getInt("xcoord"));
            node.setYcoord(rs.getInt("ycoord"));
            node.setFloor(rs.getString("floor"));
            node.setBuilding(rs.getString("building"));
            node.setNodeType(rs.getString("nodeType"));
            node.setLongName(rs.getString("longName"));
            node.setShortName(rs.getString("shortName"));
            node.setTeamAssigned(rs.getString("teamAssigned"));
        } else {

        }
        return node;
    }

    public void deleteNode(Connection conn, Node node) throws SQLException {
        String sql = NODE_DELETE;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, node.getNodeID());
        pstmt.execute();
        NodeCollection.getInstance().deleteNode(node.getNodeID());
    }
}
