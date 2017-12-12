package FoodRequestAPI.database.utility;

import FoodRequestAPI.database.objects.Edge;

public class DatabaseEdgeException extends DatabaseException {
    Edge edge;

    public DatabaseEdgeException(Edge edge, DatabaseExceptionType type) {
        super(type);

        this.edge = edge;
    }

    @Override
    public void printStackTrace() {
        System.err.println(toString());
        super.printStackTrace();
    }

    @Override
    public String toString() {
        return ("Database Exception of type " + type.name() + " for edge: " + String.format("(%s, %s, %s)", edge.getEdgeID(), edge.getNode1ID(), edge.getNode2ID()));
    }
}
