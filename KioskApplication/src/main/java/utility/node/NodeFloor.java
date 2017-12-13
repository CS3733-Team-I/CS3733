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

    /**
     * Returns the location of an image file containing a map of the corresponding floor
     * @return
     */
    public String toImagePath() {
        switch(this){
            case LOWERLEVEL_2:
                return "/images/00_thelowerlevel2.png";
            case LOWERLEVEL_1:
                return "/images/00_thelowerlevel1.png";
            case GROUND:
                return "/images/00_thegroundfloor.png";
            case FIRST:
                return "/images/01_thefirstfloor.png";
            case SECOND:
                return "/images/02_thesecondfloor.png";
            case THIRD:
                return "/images/03_thethirdfloor.png";
            default:
                return "Floor Not Found";
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


