package KioskApplication;

import database.DatabaseController;
import entity.MapEntity;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        DatabaseController.init();
        MapEntity.getInstance().readAllFromDatabase();

        Parent root = FXMLLoader.load(getClass().getResource("/view/MainWindowView.fxml"));
        primaryStage.setTitle("Iteration 2");

        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run()
            {
                System.out.println("Shutdown Hook is running !");

            }
        });
        System.out.println("Application Terminating ...");
    }
}