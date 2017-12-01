package utility;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Timer;

import static org.junit.Assert.assertEquals;

public class ResourceManagerTests extends Application {

    ResourceManager manager;

    public ResourceManagerTests() {
        manager = ResourceManager.getInstance();

        manager.getImage("/wong.jpg");
    }

    /*
     * We have to create a JavaFX runtime for these tests because
     * we need to use some JavaFX classes (like image) which
     * throw exceptions if a JavaFX application isn't running.
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        // jank
    }

    @BeforeClass
    public static void setUpClass() throws InterruptedException {
        // Initialise Java FX

        System.out.printf("About to launch FX App\n");
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(ResourceManagerTests.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
        System.out.printf("FX App thread started\n");
        Thread.sleep(500);
    }

    @Test
    public void getImageTest() {
        Image image = manager.getImage("/crazyjerry.jpg");

        assertEquals(2560.0, image.getWidth(), 0);
        assertEquals(1440.0, image.getHeight(), 0);
    }

    @Test(timeout=25)
    public void getCachedImageTest() {
        Image image = manager.getImage("/wong.jpg");

        assertEquals(383.0, image.getWidth(), 0);
        assertEquals(383.0, image.getHeight(), 0);
    }
}
