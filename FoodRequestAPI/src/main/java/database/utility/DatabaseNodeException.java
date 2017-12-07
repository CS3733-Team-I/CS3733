package database.utility;

import database.objects.Node;

public class DatabaseNodeException extends DatabaseException {
    Node node;

    public DatabaseNodeException(Node node, DatabaseExceptionType type) {
        super(type);

        this.node = node;
    }

    @Override
    public void printStackTrace() {
        System.err.println(toString());
        super.printStackTrace();
    }

    @Override
    public String toString() {
        return "Database Exception of type " + type.name() + " for node: " + node.getNodeID();
    }
}
