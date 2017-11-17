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
        DatabaseController.getInstance();
        MapEntity.getInstance().readAllFromDatabase();

        Parent root = FXMLLoader.load(getClass().getResource("/KioskApplication/view/MainWindowView.fxml"));
        primaryStage.setTitle("Iteration 1");

        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}