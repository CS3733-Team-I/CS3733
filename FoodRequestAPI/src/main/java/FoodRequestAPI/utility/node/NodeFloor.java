package FoodRequestAPI.utility.node;

import FoodRequestAPI.entity.SystemSettings;

public enum NodeFloor {
    LOWERLEVEL_2,
    LOWERLEVEL_1,
    GROUND,
    FIRST,
    SECOND,
    THIRD;

    @Override
    public String toString() {
        switch(this){
            case LOWERLEVEL_2:
                return SystemSettings.getInstance().getResourceBundle().getString("my.lowerleveltwo");
            case LOWERLEVEL_1:
                return SystemSettings.getInstance().getResourceBundle().getString("my.lowerlevelone");
            case GROUND:
                return SystemSettings.getInstance().getResourceBundle().getString("my.groundfloor");
            case FIRST:
                return SystemSettings.getInstance().getResourceBundle().getString("my.firstfloor");
            case SECOND:
                return SystemSettings.getInstance().getResourceBundle().getString("my.secondfloor");
            case THIRD:
                return SystemSettings.getInstance().getResourceBundle().getString("my.thirdfloor");
            default:
                return "Not Set: Contact Programmer";
        }
    }

    public String toLiteralString() {
        return super.toString();
    }

    public String toCSVString() {
        switch (this) {
            case LOWERLEVEL_2: return "L2";
            case LOWERLEVEL_1: return "L1";
            case GROUND: return "G";
            case FIRST: return "1";
            case SECOND: return "2";
            case THIRD: return "3";
            default: return "";
        }
    }

    public int toInt() {
        switch(this){
            case LOWERLEVEL_2:
                return -2;
            case LOWERLEVEL_1:
                return -1;
            case GROUND:
                return 0;
            case FIRST:
                return 1;
            case SECOND:
                return 2;
            case THIRD:
                return 3;
            default:
                return 9999;
        }
    }
}


