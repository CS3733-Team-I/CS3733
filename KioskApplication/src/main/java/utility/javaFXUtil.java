package utility;

import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class javaFXUtil {

    /**
     * Make a Node fade out, the input arguement is Duration...
     * e.g. someNode.makeFadeOut(Duration.millis(1000), someNode, 1, 0);
     * Above sets the transition's duration to be 1000 ms, and from opacity of 1 to 0 (== fade out)
     */
    private void makeFade(Duration duration, javafx.scene.Node node, double fromValue, double toValue) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(duration);
        fadeTransition.setNode(node);
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }
}
