package utility.node;

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
                return "Lower Level 2";
            case LOWERLEVEL_1:
                return "Lower Level 1";
            case GROUND:
                return "Ground Floor";
            case FIRST:
                return "First Floor";
            case SECOND:
                return "Second Floor";
            case THIRD:
                return "Third Floor";
            default:
                return "Not Set: Contact Programmer";
        }
    }

    public String toString1() {
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
    }
}


