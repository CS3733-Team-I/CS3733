package requests;


import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import database.objects.InterpreterRequest;
import entity.MapEntity;
import utility.Node.NodeBuilding;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpreterRequestTest {

    //setup code duplicated from TestMapEntity.java
    private MapEntity m;
    private Node n1,n2,n3,n4,n5;
    private Edge e1,e2,e3;

    @Before
    public void setup() {
        DatabaseController.getTestInstance();
        m = MapEntity.getInstance();

        n1 = new Node("NODE1", NodeFloor.GROUND);
        n2 = new Node("NODE2", NodeFloor.LOWERLEVEL_1);
        n3 = new Node("NODE3", NodeFloor.LOWERLEVEL_2);
        n4 = new Node("NODE4", NodeFloor.FIRST);

        n5 = new Node("0DEPT00399", 0, 0, NodeFloor.GROUND,
                NodeBuilding.FRANCIS45, NodeType.HALL, "Test Room", "TR", "TeamI");

        e1 = new Edge("EDGE1", "NODE1", "NODE2");
        e2 = new Edge("EDGE2", "NODE3", "NODE4");
        e3 = new Edge("EDGE3", "NODE1", "NODE4");
    }
/*

    //I want to verify that any information I put into for an interpreter request, I can also get out
    @Test
    public void testIRInterpeterID(){
        //setup
        m.addNode(n1);
        InterpreterRequest IR1=new InterpreterRequest(n1,"Nurse Joy","Japanese",1,2);
        InterpreterRequest IR2=new InterpreterRequest("Japanese",2,1);
        assertEquals(IR1.getInterpreterID(),IR2.getInterpreterID());
        //Clean up
        IR1.completeInterpreterRequest();
        m.removeNode(n1.getNodeID());
    }

    @Test
    public void testIRLanguage(){
        //setup
        m.addNode(n1);
        InterpreterRequest IR1=new InterpreterRequest(n1,"Nurse Joy","Japanese",1,2);
        InterpreterRequest IR2=new InterpreterRequest("Japanese",2,1);
        assertEquals(IR1.getLanguage(),IR2.getLanguage());
        //Clean up
        IR1.completeInterpreterRequest();
        m.removeNode(n1.getNodeID());
    }

    @Test
    public void testIREmployee(){
        //setup
        m.addNode(n1);
        InterpreterRequest IR1=new InterpreterRequest(n1,"Nurse Joy","Japanese",1,2);
        InterpreterRequest IR2=new InterpreterRequest("Japanese",2,1);
        assertEquals(IR1.getRequest().getEmployee(),IR2.getRequest().getEmployee());
        //Clean up
        IR1.completeInterpreterRequest();
        m.removeNode(n1.getNodeID());
    }

    @Test
    public void testIRRequestID(){
        //setup
        m.addNode(n1);
        InterpreterRequest IR1=new InterpreterRequest(n1,"Nurse Joy","Japanese",1,2);
        InterpreterRequest IR2=new InterpreterRequest("Japanese",2,1);
        assertEquals(IR1.getRequest().getRequestID(),IR2.getRequest().getRequestID());
        //Clean up
        IR1.completeInterpreterRequest();
        m.removeNode(n1.getNodeID());
    }

*/

    /*
    //New tests for addRequest() method
    @Test
    public void testNodeSearcher(){
        m.addNode(n1);
        m.addNode(n2);
        m.addNode(n5);
        String location = "TR";
                //DatabaseController.getNode("NODE1").getShortName();
        Node nodeLocation = n1;

        ArrayList<Node> nodes = m.getAllNodes();
        for(int i=0; i<nodes.size(); i++){
            if(nodes.get(i).getLongName().equals(location) || nodes.get(i).getLongName().equals(location)){
                nodeLocation = nodes.get(i);
            }
        }

        assertEquals(location, nodeLocation.getShortName());

        m.removeNode(n1.getNodeID());
        m.removeNode(n2.getNodeID());
        m.removeNode(n5.getNodeID());

    }
    */
}
