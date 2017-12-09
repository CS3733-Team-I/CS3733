package Search;
import database.objects.Node;
import org.junit.Before;
import org.junit.Test;
import pathfinder.FuzzySearch;

import java.util.LinkedList;

import static org.junit.Assert.assertTrue;
public class TestFuzzySearch {
    FuzzySearch fuzzySearch;
    @Before
    public void setup(){
        //fuzzySearch = new f
    }

    @Test
    public void BasicTest(){
        String search = "Obtetics Departmenr";
        String answer = "IDEPT00703";
        try{
        LinkedList<Node> list = fuzzySearch.FuzzySearch(search);
       assertTrue(answer.equals(list.get(0).getNodeID()));
        }
       catch(Exception e){
            // went to exception
        }
    }
}
