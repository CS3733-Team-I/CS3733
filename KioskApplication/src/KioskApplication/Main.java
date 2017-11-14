package KioskApplication;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.database.util.DBUtil;
import KioskApplication.entity.MapEntity;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/KioskApplication/view/MainWindowView.fxml"));
        primaryStage.setTitle("UI Framework");

        DatabaseController.init();

        // TODO do this somewhere else, and be more smart about our database access
        ArrayList<Node> nodes = DatabaseController.getAllNodes();
        for (Node node : nodes) MapEntity.getInstance().addNode(node);

        ArrayList<Edge> edges = DatabaseController.getAllEdges();
        for (Edge edge : edges) MapEntity.getInstance().addEdge(edge);


        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        DatabaseController.addRequest(0, "ISTAI00603", "test");
    }

    public static void main(String[] args) {
        launch(args);
    }
}