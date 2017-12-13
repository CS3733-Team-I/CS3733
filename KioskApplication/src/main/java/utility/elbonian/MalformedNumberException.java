package utility.elbonian;

/**
 * Exception that is thrown when a string that should represent a number (Arabic or Elbonian) is malformed.
 *
 * @version 3/18/17
 */
public class MalformedNumberException extends Exception {

    /**
     * Constructor with a description message for the malformed number Exception.
     *
     * @param message A description of the error
     */
    public MalformedNumberException(String message) {
        super(message);
    }

}
