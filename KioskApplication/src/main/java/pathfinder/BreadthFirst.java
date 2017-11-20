package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.ArrayList;
import java.util.LinkedList;

public class BreadthFirst implements SearchAlgorithm{

    /**
     * Uses the Breath First search algorithm to find a path between two nodes.
     * @param startingNode node to start navigation from
     * @param endingNode node to end navigation at
     * @return LinkedList<Edge> a linked list of edges
     * @throws PathfinderException if there are errors
     */
    //TODO this does not work yet will work on soon
    @Override
    public LinkedList<Edge> findPath(Node startNode, Node endNode)throws PathfinderException{
            MapEntity map = MapEntity.getInstance();


            StartNode startingnode = new StartNode(startNode);
            PathfinderNode endingnode = new PathfinderNode(endNode);

        //Next, get a list of all the nodes in the area you want to search (in this case, the whole map).
        //TODO: if only handling paths on single floor, only need to read in nodes for that floor.
        LinkedList<Node> allNodes = map.getAllNodes();

        if(startingnode.equals(endNode)){
            endingnode.setParentNode(startingnode);
         return endingnode.buildPath();
        }

        LinkedList<PathfinderNode> queue = new LinkedList<>();
        ArrayList<PathfinderNode> explored = new ArrayList<>();
        queue.add(startingnode);
        explored.add(startingnode);
        PathfinderNode prevousnode = startingnode;

        while(!queue.isEmpty()){
            PathfinderNode current = queue.remove();
            if(current.equals(endingnode)) {
                endingnode.setParentNode(current);
                return endingnode.buildPath();
            }
            else{
                if(getandcheckforConnectedNodes(explored,current,map).isEmpty())
                    throw new PathfinderException("No path found");
                else{

                    queue.addAll(getandcheckforConnectedNodes(explored,current,map));
                }

            }
           // current.setParentNode(prevousnode);
            explored.add(current);

        }

        return endingnode.buildPath();

    }

    LinkedList<PathfinderNode> getandcheckforConnectedNodes(ArrayList<PathfinderNode> listexplored, PathfinderNode pathfinderNode, MapEntity mapEntity){
       LinkedList<PathfinderNode> holder = pathfinderNode.getConnectedNodes(mapEntity);
       for(PathfinderNode pathfinderNode1: holder){
           for (PathfinderNode explored: listexplored){
               if(pathfinderNode1.equals(explored)){
                   holder.remove(pathfinderNode1);
               }
           }
       }
       return holder;
    }

    }

