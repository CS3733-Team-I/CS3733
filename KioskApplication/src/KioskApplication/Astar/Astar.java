package KioskApplication.Astar;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.LinkedList;

public class Astar {
    // generate path function
    public static LinkedList<Node> GeneratePath(Node startnode, Node endnode){

        // create lists for explored, frontier and unexplored nodes
        LinkedList<Node> ListExplored = new LinkedList<Node>();
        LinkedList<Node> ListofFrontier = new LinkedList<Node>();

        // list of unexplored nodes initalized as all nodes
        //TODO create function that generates list of all nodes
        LinkedList<Node> ListofUnexplored = new LinkedList<Node>();//getListofallNodes();

        // remove start node from unexplored list
        ListofUnexplored.remove(startnode);
        // add to frontier list the start node with parent node
        //TODO
     //   startnode.addtoFrontier(ListofFrontier);

        // while loop for generating path of connecting nodes
        while(true){

            // if list of frontier becomes empty break out of while loop
            if (ListofFrontier.isEmpty())
                break;

            // initalize lowest cost node
            Node lowestcost = ListofFrontier.get(0);
            //TODO
            //lowestcost.getCalculatecost();

            // go through all nodes in list and find the one with the lowest total cost and replace that as
            // the lowestcost node
            for (Node f: ListofFrontier) {
                //TODO get total cost
                //if(f.getTotalcost() < lowestcost.getTotalcost()){
                  //  f.getCalculatecost();
                  //  lowestcost = f;}
            }
                ListExplored.add(lowestcost);
            // if lowest cost node = end node break out of while loop
            if(lowestcost == endnode)
                break;

                //TODO
               // ListofFrontier.add(lowestcost.generateListofConnectedNodes());
                ListofUnexplored.remove(ListofFrontier);

        }
        // TODO replace this with generated list of nodes
        return null;
    }
}
