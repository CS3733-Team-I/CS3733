package database.objects;

import java.util.HashMap;
import java.util.LinkedList;

public class NodeCollection {
    static HashMap<String, Node> nodeHashMap = new HashMap<>();
    static NodeCollection instance = null;

    protected NodeCollection() {

    }

    public void addNode(Node node) {
        nodeHashMap.put(node.getNodeID(), node);
    }

    public  Node getNode(String id) {
        return nodeHashMap.get(id);
    }

    public void deleteNode(String id) {
        nodeHashMap.remove(id);
    }

    public static NodeCollection getInstance()  {
        if(instance == null) {
            instance = new NodeCollection();
        }
        return instance;
    }
}
