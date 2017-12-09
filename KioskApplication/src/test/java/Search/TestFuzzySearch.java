package Search;
import database.objects.Node;
import entity.SystemSettings;
import org.junit.Before;
import org.junit.Test;
import pathfinder.FuzzySearch;

import java.util.LinkedList;

import static org.junit.Assert.assertTrue;
public class TestFuzzySearch {
    FuzzySearch fuzzySearch;
    @Before
    public void setup(){
        fuzzySearch = new FuzzySearch();
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
           System.out.println("i went to exception");
        }
    }
    @Test
    public void ListTestSearch(){
        String search = "General Specialties B";
        LinkedList<String> answer = new LinkedList<>();
        //answer.add("IDEPT00403");
        //answer.add("IDEPT00303");

        try{
            LinkedList<Node> list = fuzzySearch.FuzzySearch(search);
            System.out.println(list);
            //assertTrue(answer.equals(list));
        }
        catch(Exception e){
            // went to exception
        }
    }

}
