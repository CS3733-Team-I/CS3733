package internationalization;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

public class TestInternationalization {

    @Test
    public void testUS(){
        ResourceBundle bundleUS = ResourceBundle.getBundle(
                "TestInternationalization",Locale.US);
        assertEquals("Search",bundleUS.getString("my.search"));
        assertEquals("Map",bundleUS.getString("my.map"));
        assertEquals("Current Waypoints:",bundleUS.getString("my.waypoints"));
        assertEquals("Navigate",bundleUS.getString("my.navigate"));
        assertEquals("Clear",bundleUS.getString("my.clear"));
    }
}
