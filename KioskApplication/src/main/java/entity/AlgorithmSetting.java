package entity;

public class AlgorithmSetting {
    private String algorithm;

    private static class AlgorithmSettingSingleton {
        private static final entity.AlgorithmSetting _instance = new entity.AlgorithmSetting();

    }

    private AlgorithmSetting() {
        this.algorithm = "Astar"; // Default
    }

    public static entity.AlgorithmSetting getInstance() {
        return entity.AlgorithmSetting.AlgorithmSettingSingleton._instance;
    }

    public void changeAlgorithm(String a){
        this.algorithm = a;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
