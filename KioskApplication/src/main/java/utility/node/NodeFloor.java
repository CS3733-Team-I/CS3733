package utility.node;

import entity.SystemSettings;

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
                return SystemSettings.getInstance().getResourceBundle().getString("lowerLevelTwo");
            case LOWERLEVEL_1:
                return SystemSettings.getInstance().getResourceBundle().getString("lowerLevelOne");
            case GROUND:
                return SystemSettings.getInstance().getResourceBundle().getString("groundFloor");
            case FIRST:
                return SystemSettings.getInstance().getResourceBundle().getString("firstFloor");
            case SECOND:
                return SystemSettings.getInstance().getResourceBundle().getString("secondFloor");
            case THIRD:
                return SystemSettings.getInstance().getResourceBundle().getString("thirdFloor");
            default:
                return "Not Set: Contact Programmer";
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


