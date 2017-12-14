package FoodRequestAPI.utility.node;

public enum NodeBuilding {
    FRANCIS45,
    FRANCIS15,
    TOWER,
    SHAPIRO,
    BTM;

    @Override
    public String toString() {
        switch (this) {
            case FRANCIS45: return "45 Francis";
            case FRANCIS15: return "15 Francis";
            case TOWER: return "Tower";
            case SHAPIRO: return "Sharpiro";
            case BTM: return "BTM";
            default: return "";
        }
    }
}
