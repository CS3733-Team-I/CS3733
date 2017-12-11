package pathfinder;


import database.objects.Node;
import database.objects.Request;
import entity.MapEntity;
import entity.RequestEntity;
import entity.searchEntity.ISearchEntity;
import entity.searchEntity.SearchNode;

import java.util.*;

public class NodeFuzzySearch {

    /**
     * Fuzzy search
     * goes through all nodes long names and finds the top 5 matches to all nodes
     *
     * @param inputtext
     * @return Linked list of 5 top search result nodes
     */
    public static LinkedList<ISearchEntity> fuzzySearch(String inputtext) throws Exception {// add boolean
        if (!inputtext.isEmpty()) {
            // put all nodes in hash map
            MapEntity map = MapEntity.getInstance();
            //LinkedList<Node> mapAllNodes = map.getAllNodes();
            HashMap<String, Node> allNodes = map.getAllNodesHM();
            RequestEntity request = RequestEntity.getInstance();
            HashMap<String, Request> all = request.getallRequestsHM();
            inputtext = inputtext.replaceAll("\\s+","");
            String[] input = inputtext.split("");

            // go through all nodes and get hightest match in a hashmap with key of node
            Map<String, Integer> sortedMap = new HashMap<>();
            int matched = 0;
            for (String key : allNodes.keySet()) {
                matched = 0;
               String longname = allNodes.get(key).getLongName().replaceAll("\\s+","");
                String[] longName = longname.split("");
               // for (int i = 0; i < longName.length; i++) {
                    for (int i=0; i<input.length;i++){
                        if (longname.toLowerCase().contains(input[i].toLowerCase()))
                            if (matched < input.length) {
                                matched++;
                            } else {
                                // do nothing
                            }
                        if(i>=longName.length) {
                            // do nothing
                            }
                        else{
                            if (longName[i].toLowerCase().equals(input[i].toLowerCase())) {
                                matched++;}
                        }
                }
                sortedMap.put(key, matched);
            }
            // now sort hashmap from highest to lowest
            int min = 0;
            List<String> sorted = new ArrayList<>();
            for (String key : sortedMap.keySet()) {
                if (sortedMap.get(key) >= min) {
                    sorted.add(0, key);
                    min = sortedMap.get(key);
                } else {
                    sorted.add(sorted.size(), key);
                }
            }
            // get the top 5 from sorted arraylist
            LinkedList<Node> bestmatch = new LinkedList<>();
            for (int i = 0; i < 5; i++) {
                bestmatch.add(allNodes.get(sorted.get(i)));
            }
            LinkedList<ISearchEntity> searchMatch = new LinkedList<>();
            for(Node matchedNode : bestmatch) {
                searchMatch.add(new SearchNode(matchedNode));
            }
            return searchMatch;
        } else {
            // input is 0 return exceptipon
            throw new Exception("No Input Text");
        }
    }

    }
