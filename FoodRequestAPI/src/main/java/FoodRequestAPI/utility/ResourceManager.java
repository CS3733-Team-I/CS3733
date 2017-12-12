package FoodRequestAPI.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

import java.util.HashMap;

public class ResourceManager {

    HashMap<String, Image> imageCache;
    HashMap<String, FXMLLoader> loaderCache;

    private ResourceManager() {
        imageCache = new HashMap<>();
        loaderCache = new HashMap<>();
    }

    private static class SingletonHelper {
        public static ResourceManager _instance = new ResourceManager();
    }

    public static ResourceManager getInstance() {
        return SingletonHelper._instance;
    }

    public FXMLLoader getFXMLLoader(String path) {
        if (loaderCache.containsKey(path)) {
            return loaderCache.get(path);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loaderCache.put(path, loader);
        return loader;
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
