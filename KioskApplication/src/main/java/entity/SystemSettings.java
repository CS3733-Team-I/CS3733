package entity;

import database.objects.Node;
import pathfinder.*;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SystemSettings {
    private Preferences prefs;
    private SearchAlgorithm algorithm;
    private Node defaultnode;
    private int beamWidth;
    private ResourceBundle resourceBundle;

    private static class SystemSettingsSingleton {
        private static final SystemSettings _instance = new SystemSettings();

    }

    private SystemSettings() {
        this.prefs = Preferences.userNodeForPackage(SystemSettings.class);
        this.setAlgorithm(this.prefs.get("searchAlgorithm", "A*"));   //Retrieve saved algorithm setting;
        this.setDefaultnode(this.prefs.get("defaultNode","IDEPT00403"));
        this.setBeamWidth(this.prefs.get("beamWidth", "3"));
        this.setResourceBundle(this.prefs.get("Internationalization","English"));
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

    public void setDefaultnode(String defaultnodeID){
        MapEntity map = MapEntity.getInstance();
        for(Node node:map.getAllNodes()){
            if(defaultnodeID.equals(node.getNodeID())) {
                this.defaultnode = node;
                this.prefs.put("defaultNode", node.getNodeID());
                return;
            }
        }
    }

    public Node getDefaultnode() {
        return defaultnode;
    }

    /**
     * set beam width from UI
     * @param beamWidth
     */
    public void setBeamWidth(String beamWidth) {
        this.beamWidth = Integer.parseInt(beamWidth);
        this.prefs.put("beamWidth",beamWidth);
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
            case "France":
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
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
