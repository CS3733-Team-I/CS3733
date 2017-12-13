package entity;

import database.connection.NotFoundException;
import database.objects.Node;

import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SystemSettings extends Observable {
    private Preferences prefs;
    private ResourceBundle resourceBundle;

    private static class SystemSettingsSingleton {
        private static final SystemSettings _instance = new SystemSettings();

    }

    private SystemSettings () {
        super();
        MapEntity map = MapEntity.getInstance();
        this.prefs = Preferences.userNodeForPackage(SystemSettings.class);
        this.setResourceBundle("English");
    }

    public static SystemSettings getInstance() {
        return SystemSettingsSingleton._instance;
    }

    public void setResourceBundle(String resourceBundle) {
        switch(resourceBundle){
            case "English":
                this.resourceBundle = ResourceBundle.getBundle(
                    "Internationalization", Locale.US);
                this.prefs.put("Internationalization", "English");
                break;
            case "French":
                this.resourceBundle = ResourceBundle.getBundle(
                        "Internationalization",Locale.FRANCE);
                this.prefs.put("Internationalization", "France");
                break;
            default:
                this.resourceBundle = ResourceBundle.getBundle(
                        "Internationalization", Locale.US);
                this.prefs.put("Internationalization", "English");
                break;
        }
        this.setChanged();
        notifyObservers();
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
