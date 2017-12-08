package pathfinder;

import com.sun.deploy.util.StringUtils;
import database.objects.Node;
import entity.MapEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.lang.Object;
import org.apache.commons.codec.language.DoubleMetaphone;

public class FuzzySearch {

   // private DoubleMetaphone metaphone;
    private DoubleMetaphone metaphone;
    /**
     * Fuzzy search
     * using metaphone
     * @param inputtext
     * @return
     */
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
        metaphone = new DoubleMetaphone();
        //Node bestmatch = allNodes.get(allNodes.keySet().iterator().next());
        String[] input= inputtext.split("");

        if(input.length >0) {
            Map<String,Integer> sortedMap = new HashMap<>();
            int matched = 0;
            for (String key : allNodes.keySet()) {
                String[] longname = allNodes.get(key).getLongName().split("");
                for (int i = 0; i < longname.length; i++) {
                    matched = 0;
                    for (String text : input) {
                        if (metaphone.isDoubleMetaphoneEqual(text, longname[i])) {
                            matched++;
                        }
                    }
                    sortedMap.put(key, matched);
                }
            }
            int min = 0;
            String match = allNodes.keySet().iterator().next();
           for (String key: sortedMap.keySet()){
                if(sortedMap.get(key)>min){
                    match=key;
                    min=sortedMap.get(key);
                }
            }

            return allNodes.get(match);
        }
        else{
        return null;}
    }
}
