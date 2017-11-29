package utility;

import javafx.scene.image.Image;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

public class ResourceManager {

    HashMap<String, Image> imageCache;

    private ResourceManager() {
        imageCache = new HashMap<>();
    }

    private static class SingletonHelper {
        public static ResourceManager _instance = new ResourceManager();
    }

    public static ResourceManager getInstance() {
        return SingletonHelper._instance;
    }

    public Image getImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        Image image = new Image(path);

        imageCache.put(path, image);
        return image;
    }
}
