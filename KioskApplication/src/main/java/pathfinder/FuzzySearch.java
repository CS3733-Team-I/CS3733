package pathfinder;

import com.sun.deploy.util.StringUtils;
import database.objects.Node;
import entity.MapEntity;

import java.util.ArrayList;
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
     * @return Linked list of 5 top search result nodes
     */
    public LinkedList<Node> FuzzySearch(String inputtext) throws Exception{
        if (!inputtext.isEmpty()){
        // put all nodes in hash map
        MapEntity map = MapEntity.getInstance();
        //LinkedList<Node> mapAllNodes = map.getAllNodes();
        HashMap<String, Node> allNodes = map.getAllNodesHM();

        metaphone = new DoubleMetaphone();
        // spit input text
        String[] input= inputtext.split("");
        // check for

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
            ArrayList<Node> sorted = new ArrayList<>();
           for (String key: sortedMap.keySet()){
                if(sortedMap.get(key)>min){
                    sorted.add(0,allNodes.get(key));
                    min=sortedMap.get(key);
                }
                else{
                sorted.add(allNodes.get(key));
                }
            }
            LinkedList<Node> bestmatch = new LinkedList<>();
            for (int i=0;i<5;i++){
                bestmatch.add(sorted.get(i));
            }
            return bestmatch;
        }
        else{
            // input is 0 return exceptipon
            throw new Exception("No Input Text"); }
    }
}
