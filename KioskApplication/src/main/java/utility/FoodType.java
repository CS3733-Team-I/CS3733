package utility;

public enum FoodType {
    ENTREE,
    SIDE,
    DRINK;

    //TODO: Language support
    @Override
    public String toString() {
        switch(this) {
            case ENTREE:
                return "Entree";
            case SIDE:
                return "Side";
            case DRINK:
                return "Drink";
            default:
                return "Not Set: Contact Programmer";
        }
    }
}
