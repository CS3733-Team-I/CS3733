package pathfinder;

public class PathfinderException extends Exception{

    private String message;

    public PathfinderException(String message){
        this.message = message;
        //System.out.println(message);
    }

    public String getMessage() {
        return message;
    }
}
