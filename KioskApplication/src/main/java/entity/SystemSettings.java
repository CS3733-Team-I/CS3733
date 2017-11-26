package entity;

import pathfinder.*;

import java.util.prefs.Preferences;

public class SystemSettings {
    private Preferences prefs;
    private SearchAlgorithm algorithm;

    private static class SystemSettingsSingleton {
        private static final SystemSettings _instance = new SystemSettings();

    }

    private SystemSettings() {
        this.prefs = Preferences.systemNodeForPackage(SystemSettings.class);
        this.setAlgorithm(this.prefs.get("searchAlgorithm", "A*"));   //Retrieve saved algorithm setting;
                                                                              //if not set, default to A*
        System.out.println(this.prefs.get("searchAlgorithm", "A*"));
    }

    public static SystemSettings getInstance() {
        return SystemSettingsSingleton._instance;
    }

    public void setAlgorithm(String algorithmString){
        switch(algorithmString){
            case "Astar":
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
            default:
                break;  //If the input string is invalid, leave the set search algorithm unchanged.
        }
    }

    public SearchAlgorithm getAlgorithm() {
        return algorithm;
    }
}
