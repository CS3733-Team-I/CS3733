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
}


