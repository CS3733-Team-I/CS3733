package entity;

import pathfinder.A_star;
import pathfinder.SearchAlgorithm;

public class AlgorithmSetting {
    private SearchAlgorithm algorithm;

    private static class AlgorithmSettingSingleton {
        private static final entity.AlgorithmSetting _instance = new entity.AlgorithmSetting();

    }

    private AlgorithmSetting() {
        this.algorithm = new A_star(); // Default
    }

    public static entity.AlgorithmSetting getInstance() {
        return entity.AlgorithmSetting.AlgorithmSettingSingleton._instance;
    }

    public void changeAlgorithm(SearchAlgorithm a){
        this.algorithm = a;
    }

    public SearchAlgorithm getAlgorithm() {
        return algorithm;
    }
}
