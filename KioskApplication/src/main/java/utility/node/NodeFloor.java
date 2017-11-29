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
}


