package KioskApplication.Astar;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.LinkedList;

public class Astar {
    // generate path function
    public static LinkedList<Node> GeneratePath(Node startnode, Node endnode){
        LinkedList<Node> ListExplored = new LinkedList<Node>();
        LinkedList<Node> ListofFrontier = new LinkedList<Node>();
        //TODO create function that generates list of all nodes
        LinkedList<Node> ListofUnexplored = new LinkedList<Node>();//getListofallNodes();

        ListofUnexplored.remove(startnode);
        //TODO
     //   startnode.addtoFrontier(ListofFrontier);

        while(true){

            if (ListofFrontier.isEmpty())
                break;

            Node lowestcost = ListofFrontier.get(0);
            //TODO
            //lowestcost.calculatecost();

        }

        return null;
    }
}
