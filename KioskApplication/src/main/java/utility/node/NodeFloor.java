package utility.node;

import entity.SystemSettings;

public enum NodeFloor {
    LOWERLEVEL_2,
    LOWERLEVEL_1,
    GROUND,
    FIRST,
    SECOND,
    THIRD;

    private ResourceBundle rB;

    @Override
    public String toString() {
        InternationalizationEntity intEntity = InternationalizationEntity.getInstance();
        rB = intEntity.getCurrentLanguage();
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

    /*public String toString1() {
        switch(this){
            case LOWERLEVEL_2:
                return "LOWERLEVEL_2";
            case LOWERLEVEL_1:
                return "LOWERLEVEL_1";
            case GROUND:
                return "GROUND";
            case FIRST:
                return "FIRST";
            case SECOND:
                return "SECOND";
            case THIRD:
                return "THIRD";
            default:
                return "Not Set: Contact Programmer";
        }
    }*/
}


