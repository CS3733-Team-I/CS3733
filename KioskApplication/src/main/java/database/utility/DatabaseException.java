package database.utility;

public class DatabaseException extends Exception {
    DatabaseExceptionType type;

    public DatabaseException(DatabaseExceptionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Database Error: " + type.toString();
    }

    public DatabaseExceptionType getType() {
        return type;
    }
}
