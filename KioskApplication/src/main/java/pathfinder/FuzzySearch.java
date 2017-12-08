package pathfinder;

import com.sun.deploy.util.StringUtils;
import database.objects.Node;
import entity.MapEntity;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.codec.language.DoubleMetaphone;

public class FuzzySearch {

    private DoubleMetaphone metaphone;

    public Node FuzzySearch(String inputtext){
        // put all nodes in hash map
        //TODO probably better way of initalizing
        MapEntity map = MapEntity.getInstance();
        LinkedList<Node> mapAllNodes = map.getAllNodes();
        HashMap<String, Node> allNodes = new HashMap<>();

        // put all nodes in list
        for(Node node :mapAllNodes){
            allNodes.put(node.getNodeID(),node);
        }

        String[] input= inputtext.split("");


            for(String key: allNodes.keySet()){
                    for (String text:input){
                   // key.
            }
        }

        int min = 15;//initial value
        int testVal=0;

        //return matchedPerson;
        return null;
    }
}
