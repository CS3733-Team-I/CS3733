package utility.node;

public enum TeamAssigned {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    W;

    public static TeamAssigned fromString(String team) {
        switch (team) {
            case "Team A": return A;
            case "Team B": return B;
            case "Team C": return C;
            case "Team D": return D;
            case "Team E": return E;
            case "Team F": return F;
            case "Team G": return G;
            case "Team H": return H;
            case "Team I": return I;
            case "Team W": return W;
            default: return W;
        }
    }
}
