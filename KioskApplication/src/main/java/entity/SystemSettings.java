package entity;

import database.connection.NotFoundException;
import database.objects.Node;
import javafx.beans.property.SimpleObjectProperty;
import pathfinder.*;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SystemSettings extends Observable {
    private Preferences prefs;

    private static final String searchAlgorithmKey = "searchAlgorithm";
    private SearchAlgorithm algorithm;
    private String defaultSearchAlgorithm = "A*";

    private static final String kioskLocationKey = "kioskLocation";
    private SimpleObjectProperty<Node> kioskLocationProperty;
    private String defaultKioskLocationID = "IHALL00303";

    private static final String beamWidthKey = "beamWidth";
    private int beamWidth;
    private String defaultBeamWidth = "3";

    private ResourceBundle resourceBundle;

    private static class SystemSettingsSingleton {
        private static final SystemSettings _instance = new SystemSettings();

    }

    private SystemSettings () {
        super();

        kioskLocationProperty = new SimpleObjectProperty<>();

        MapEntity map = MapEntity.getInstance();
        this.prefs = Preferences.userNodeForPackage(SystemSettings.class);
        this.setAlgorithm(this.prefs.get(this.searchAlgorithmKey, "A*"));   //Retrieve saved algorithm setting;

        //Retrieve saved kiosk location
        try{
            this.kioskLocationProperty.set(map.getNode(this.prefs.get(this.kioskLocationKey,this.defaultKioskLocationID)));
        }
        catch(NotFoundException exception){
            //If the saved location is not valid, clear it and try again with the default location
            try {
                System.err.println("Error: saved kiosk location does not exist.  Resetting kiosk location to default.");
                this.prefs.remove(this.kioskLocationKey);
                this.kioskLocationProperty.set(map.getNode(this.prefs.get(this.kioskLocationKey,this.defaultKioskLocationID)));
            } catch (NotFoundException e) {
                //If the default node doesn't exist either, print an error and reset the default to something that does exist.
                System.err.println("Error: default kiosk location does not exist.  Getting a new default from the map.");
                //Change the default node to the first node in the map.
                this.defaultKioskLocationID = map.getAllNodes().getFirst().getNodeID();
                //Now, we should be able to get the new default location.
                try {
                    this.kioskLocationProperty.set(map.getNode(this.prefs.get(this.kioskLocationKey,this.defaultKioskLocationID)));
                } catch (NotFoundException e1) {
                    //If THAT doesn't work, something very strange is happening.
                    e1.printStackTrace();
                }
            }
        }
        this.setBeamWidth(this.prefs.get(this.beamWidthKey, "3"));
        //this.setResourceBundle(this.prefs.get("Internationalization","English"));
        this.setResourceBundle("English");
        //if not set, default to A*
//        System.out.println(this.prefs.get("searchAlgorithm", "A*"));  //For debugging
    }

    public static SystemSettings getInstance() {
        return SystemSettingsSingleton._instance;
    }

    public void setAlgorithm(String algorithmString){
        switch(algorithmString){
            case "A*":
                this.algorithm = new A_star();
                this.prefs.put("searchAlgorithm", "A*");
                break;
            case "Dijkstra":
                this.algorithm = new Dijkstra();
                this.prefs.put("searchAlgorithm", "Dijkstra");
                break;
            case "BFS":
                this.algorithm = new BreadthFirst();
                this.prefs.put("searchAlgorithm", "BFS");
                break;
            case "DFS":
                this.algorithm = new DepthFirst();
                this.prefs.put("searchAlgorithm", "DFS");
                break;
            case "Beam":
                this.algorithm = new Beam();
                this.prefs.put("searchAlgorithm", "Beam");
                break;
            case "BestFS":
                this.algorithm = new BestFirst();
                this.prefs.put("searchAlgorithm", "BestFS");
                break;
            default:
                break;  //If the input string is invalid, leave the set search algorithm unchanged.
        }
    }

    public SearchAlgorithm getAlgorithm() {
        return algorithm;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public void setKioskLocation(String kioskLocationID){
        MapEntity map = MapEntity.getInstance();
        try {
            this.kioskLocationProperty.set(map.getNode(kioskLocationID));
            this.prefs.put(this.kioskLocationKey, kioskLocationID);
        } catch (NotFoundException e) {
            System.err.println("Error: specified kiosk location does not exist.  Kiosk location not changed.");
        }
    }

    public Node getKioskLocation() {
        return kioskLocationProperty.get();
    }

    public SimpleObjectProperty<Node> kioskLocationPropertyProperty() {
        return kioskLocationProperty;
    }

    /**
     * set beam width from UI
     * @param beamWidth
     */
    public void setBeamWidth(String beamWidth) {
        this.beamWidth = Integer.parseInt(beamWidth);
        this.prefs.put(this.beamWidthKey,beamWidth);
    }

    /**
     * get beam width from UI
     * @return
     */
    public int getBeamWidth() {
        return beamWidth;
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

    public void updateDistance() {
        MapEntity.getInstance().updateDistanceFromKisok(getKioskLocation());
    }
}
