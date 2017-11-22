package requests;


import database.DatabaseController;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Node;
import entity.MapEntity;
import org.junit.Before;
import org.junit.Test;
import utility.Node.NodeBuilding;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import utility.Request.Language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

    @Test
    public void testIDGenerationHeader(){
        InterpreterRequest iR = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        assertEquals("Int",iR.getRequestID().substring(0,3));
    }

    @Test
    public void testIDGenerationTimeDifference(){
        InterpreterRequest iR1 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        InterpreterRequest iR2 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        assertNotEquals(iR1.getRequestID(),iR2.getRequestID());
    }

    @Test
    public void testIDGenerationSizeLimit(){
        InterpreterRequest iR1 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        System.out.println(iR1.getRequestID());
        System.out.println(iR1.getRequestID().length());
        System.out.println(iR1.getSubmittedTime().toString().length());
    }
}
