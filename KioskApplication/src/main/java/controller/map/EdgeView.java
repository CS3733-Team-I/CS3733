package controller.map;

import database.objects.Edge;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class EdgeView {

    Edge edge;
    boolean editable;

    NodesEdgesView parent;

    public EdgeView(Edge edge, boolean editable) {
        this.edge = edge;
        this.editable = editable;
    }


    @FXML
    private void initialize() {
        if (this.editable) {
            container.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event){
                    // Let Parent know it was clicked
                    parent.mapEdgeClicked(edge);
                    event.consume();
                }
            });

        }
    }

}
