package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Beam implements SearchAlgorithm {
    @Override
    public LinkedList<Edge> findPath(Node startNode, Node endNode) throws PathfinderException {
        //TODO: write algorithm
        MapEntity map = MapEntity.getInstance();

        StartNode startingNode = new StartNode(startNode);
        PathfinderNode endingNode = new PathfinderNode(endNode);

        // if end == start then finish and build path
        if(startingNode.getNode().getNodeID().equals(endNode.getNodeID()))
            return startingNode.buildPath();
        // a hash map is used to store nodes that have been visited
          HashMap<String, PathfinderNode> closedList = new HashMap<>();
         HashMap<String, PathfinderNode> beam = new HashMap<>();
         HashMap<String, PathfinderNode> set = new HashMap<>();
      //  private static final Integer[] heuristic = {1, 0, 1, 1, 2, 1, 1, 2, 2, 1};

   // public static Integer search(Graph G, Integer start, Integer goal, Integer beamWidth) {
     //   int g = 0;
     //   int v = G.V();
    //    int initial = v + 1; // just the symbol of null
       // int fail = initial + 1;
        closedList.put(startingNode.getNode().getNodeID(), startingNode);
        beam.put(startingNode.getNode().getNodeID(), startingNode);

        // else go to search
        // main Loop
        while (beam.size() != 0) {
            // System.out.println("Beam :" + beam);
            // System.out.println("ClosedList :" + closedList);
            set.clear();
            for (PathfinderNode node : beam.values()) {
                for (PathfinderNode neighbor : getAndCheckForConnectedNodes(closedList, node, map)) {
                    if (neighbor.getNode().getNodeID().equals(endingNode.getNode().getNodeID())) {
                        return neighbor.buildPath();
                    }
                    set.put(neighbor.getNode().getNodeID(), neighbor);
                }
            }
            //System.out.println("Set : " + set);
            beam = new HashMap<>();
            // g = g + 1;
            while ((set.size() != 0) && (2 > beam.size())) {
                //  HashMap<String, PathfinderNode> heuristicValue = new HashMap<>();
                for (PathfinderNode key : set.values()) {
                    // heuristicValue.put(key, set.get(key));
                    Integer minIndex = heuristic(set.get(key.getNode().getNodeID()), endingNode);
                    Iterator<String> keys = set.keySet().iterator();
                    while (keys.hasNext()) {
                        String key1 = keys.next();
                        if (heuristic(set.get(key1), endingNode) >= minIndex)
                            keys.remove();
                    }

                    //if (!closedList.containsKey(key.getNode().getNodeID())) {
                    //    closedList.put(key.getNode().getNodeID(), set.get(key.getNode().getNodeID()));
                   //     beam.put(key.getNode(), set.get(minIndex));
                  //  }
                }
            }
        }
        throw new PathfinderException("No path exists");

    }

    /*public static Integer compare_hashMap_min(HashMap<Integer, Integer> scores) {
        Collection c = scores.values();
        Integer minvalue = (Integer) Collections.min(c);
        Integer minIndex = 0;
        Set<Integer> scores_set = scores.keySet();
        Iterator<Integer> scores_it = scores_set.iterator();
        while(scores_it.hasNext()) {
            Integer id = scores_it.next();
            Integer value = scores.get(id);
            if (value == minvalue) {
                minIndex = id;
                break;
            }
        }
        return minIndex;
    }*/
    /**
     * Estimates the distance between two nodes.
     * @param node1 a pathfinding node
     * @param node2 another pathfinding node
     * @return  An estimate of the distance between node1 and node2.
     */
    private int heuristic(PathfinderNode node1, PathfinderNode node2){
        double xDistance = node1.getNode().getXcoord() - node2.getNode().getXcoord();
        double yDistance = node1.getNode().getYcoord() - node2.getNode().getYcoord();
        return (int) Math.sqrt((Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
    }

    /**
     * check for the connected nodes in a list and add to connected nodes list
     * and the parent node if it there is none
     * @param listExplored
     * @param pathfinderNode
     * @param mapEntity
     * @return
     */
    private LinkedList<PathfinderNode> getAndCheckForConnectedNodes(HashMap<String,PathfinderNode> listExplored, PathfinderNode pathfinderNode, MapEntity mapEntity){
        // initalize connected nodes list from current node
        LinkedList<PathfinderNode> connectedNodes = pathfinderNode.getConnectedNodes(mapEntity);
        LinkedList<PathfinderNode> holder = new LinkedList<>();
        holder.addAll(connectedNodes);
        for(PathfinderNode pathfinderNode1: connectedNodes){
            // add parent node for edge generation if partent node is not created
            if(pathfinderNode1.parentNode == null && pathfinderNode !=null)
                pathfinderNode1.parentNode = pathfinderNode;
            for (PathfinderNode explored: listExplored.values()){
                // remove from queue list if in explored list
                if(pathfinderNode1.node.getNodeID().equals(explored.getNode().getNodeID())){
                    holder.remove(pathfinderNode1);
                }
            }
        }
        return holder;
    }
}
