package entity;

import database.objects.Node;
import pathfinder.*;

import java.util.prefs.Preferences;

public class SystemSettings {
    private Preferences prefs;
    private SearchAlgorithm algorithm;
    private Node defaultnode;

    private static class SystemSettingsSingleton {
        private static final SystemSettings _instance = new SystemSettings();

    }

    private SystemSettings() {
        this.prefs = Preferences.userNodeForPackage(SystemSettings.class);
        this.setAlgorithm(this.prefs.get("searchAlgorithm", "A*"));   //Retrieve saved algorithm setting;
        this.setDefaultnode(this.prefs.get("defaultNode","IDEPT00403"));
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
}
