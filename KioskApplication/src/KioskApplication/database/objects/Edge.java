package database.objects;

public class Edge {
    private String edgeID; //ID of the edge
    private Node node1; //one of the nodes that is part of the edge
    private Node node2; //one of the nodes that is part of the edge

    //initialize the edge with an id, and two nodes
    public Edge(String edgeID, Node node1, Node node2) {
        this.edgeID = edgeID;
        this.node1 = node1;
        this.node2 = node2;
    }

    //will throw exception if node does not exist
    public Edge(String edgeID, String node1ID, String node2ID) {
        //converts each id into an Node object
        NodeCollection collection = NodeCollection.getInstance();
        node1 = collection.getNode(node1ID);
        node2 = collection.getNode(node2ID);
    }

    public String getEdgeID() {
        return edgeID;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public void setNode1(Node node1) {
        this.node1 = node1;
    }

    public void setNode2(Node node2) {
        this.node2 = node2;
    }

    public void setNode1(String id) {
        node1 =  NodeCollection.getInstance().getNode(id);
    }

    public void setNode2(String id) {
        node2 =  NodeCollection.getInstance().getNode(id);
    }
}
