package requests;


import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import database.objects.InterpreterRequest;
import entity.MapEntity;
import utility.Language;
import utility.NodeBuilding;
import utility.NodeFloor;
import utility.NodeType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class InterpreterRequestTest {

    @Test
    public void testIDGenerationHeader(){
        InterpreterRequest iR = new InterpreterRequest("current loc","nurse","", Language.ARABIC);
        assertEquals("Int",iR.getRequestID().substring(0,3));
    }

    @Test
    public void testIDGenerationTimeDifference(){
        InterpreterRequest iR1 = new InterpreterRequest("current loc","nurse","", Language.ARABIC);
        InterpreterRequest iR2 = new InterpreterRequest("current loc","nurse","", Language.ARABIC);
        assertNotEquals(iR1.getRequestID(),iR2.getRequestID());
    }

}
