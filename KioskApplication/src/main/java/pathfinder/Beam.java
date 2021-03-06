package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.SystemSettings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Beam implements SearchAlgorithm {
    /**
     * beam search
     * searchs connected nodes in beam map checks if they are the end node, if not then add those nodes to set.
     * compare the heristic values nodes in set and put the lowest heristic values in beam list
     * beamwidth = controls the beam size to search from set in admin ui system settings
     * @param startNode node that the path should start at
     * @param endNode node that the path should end at
     * @return
     * @throws PathfinderException
     */
    @Override
    public LinkedList<Edge> findPath(Node startNode, Node endNode, boolean wheelchairAccessible) throws PathfinderException {
        int beamwidth = SystemSettings.getInstance().getBeamWidth();
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

         // put start node in closesdlist and beam
        closedList.put(startingNode.getNode().getNodeID(), startingNode);
        beam.put(startingNode.getNode().getNodeID(), startingNode);

        // go to search
        // main Loop
        while (beam.size() != 0) {
            // System.out.println("Beam :" + beam);
            // System.out.println("ClosedList :" + closedList);
            set.clear();
            for (PathfinderNode node : beam.values()) {
                if(node==null)
                    throw new PathfinderException("null node");
                for (PathfinderNode neighbor : getAndCheckForConnectedNodes(closedList, node, wheelchairAccessible)) {
                    if (neighbor.getNode().getNodeID().equals(endingNode.getNode().getNodeID())) {
                        return neighbor.buildPath();
                    }
                    set.put(neighbor.getNode().getNodeID(), neighbor);
                }
            }
            //System.out.println("Set : " + set);
            // clear beam
            beam = new HashMap<>();
            // go through set and add to beam map
            while ((set.size() != 0) && (beamwidth > beam.size())) {
                  HashMap<String, PathfinderNode> heuristicValue = new HashMap<>();
                  // for heristicvalue map
                for (String key : set.keySet()) {
                     heuristicValue.put(key, set.get(key));
                }
                    // get min heristic value
                    String minIndex = compare_hashMap_min(heuristicValue, endingNode);
                    Iterator<String> keys = set.keySet().iterator();
                    // remove that min value from keys in set
                    while (keys.hasNext()) {
                       String key1 = keys.next();
                        if (key1 == minIndex)
                            keys.remove();
                    }
                    // add to beam and closed list
                    if (!closedList.containsKey(minIndex)) {
                        closedList.put(minIndex, heuristicValue.get(minIndex));
                        beam.put(minIndex, heuristicValue.get(minIndex));
                   }
                }
            }
            // no path exists
        throw new PathfinderException("No path exists");
    }

    /**
     * Estimates the distance between two nodes.
     * @param node1 a pathfinding node
     * @param node2 another pathfinding node
     * @return  An estimate of the distance between node1 and node2.
     */
    private int heuristic(PathfinderNode node1, PathfinderNode node2){
        double xDistance = node1.getNode().getXcoord() - node2.getNode().getXcoord();
        double yDistance = node1.getNode().getYcoord() - node2.getNode().getYcoord();
        int totaldistance = (int) Math.sqrt((Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
        // if the distance is 0 but the node is not the same then the current node is on a different floor
        if (totaldistance==0&&!node1.getNode().getNodeID().equals(node2.getNode().getNodeID()) ){
            totaldistance = totaldistance+40;
        }
        return totaldistance;
    }

    /**
     * check for the connected nodes in a list and add to connected nodes list
     * and the parent node if it there is none
     * @param listExplored
     * @param pathfinderNode
     * @param wheelchairAccessible
     * @return
     */
    private LinkedList<PathfinderNode> getAndCheckForConnectedNodes(HashMap<String,PathfinderNode> listExplored, PathfinderNode pathfinderNode, boolean wheelchairAccessible){
        // initalize connected nodes list from current node
        LinkedList<PathfinderNode> connectedNodes = pathfinderNode.getConnectedNodes(wheelchairAccessible);
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

    /**
     * compares all heristic values in hash map and returns string index of the one with the lowest
     * @param scores
     * @param endnode
     * @return
     */
    private String compare_hashMap_min(HashMap<String, PathfinderNode> scores, PathfinderNode endnode) {
        //if there is only one in the index then that is the lowest
        // set and return
        if (scores.size()==1){
            for (String key: scores.keySet()){
                return key;
            }
        }
        else{// go through and compare heristic values
          String minIndex = scores.entrySet().iterator().next().getKey();
        Integer minvalue = heuristic(scores.get(minIndex),endnode);
        //minIndex = scores_it.next();
            for (String key: scores.keySet()){
              int h = heuristic(scores.get(key),endnode);
            if (h <= minvalue){
                // herestic is lower than minvalue than set that one to minindex
                minvalue = h;
                minIndex = key;
            }
        }
        return minIndex;}
        return "";
    }
}
