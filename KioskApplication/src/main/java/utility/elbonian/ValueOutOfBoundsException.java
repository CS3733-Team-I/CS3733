package utility.elbonian;

/**
 * An Exception that is thrown when the number has a value that cannot be represented
 * by the Elbonian Numerals (ie. negative, zero, or greater than 3999).
 *
 * @version 3/18/17
 */
public class ValueOutOfBoundsException extends Exception {

    /**
     * Constructor with a descriptive message for the value out of bounds exception.
     *
     * @param message A description of the error
     */
    public ValueOutOfBoundsException(String message) {
        super(message);
    }

}
