import controller.MainWindowController;
import email.*;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utility.csv.CsvFileUtil;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MapEntity.getInstance().readAllFromDatabase();
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCSVs();

        SystemSettings.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindowView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Final Iteration");

        Scene mainScene = new Scene(root, 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setOnHidden(e -> ((MainWindowController)loader.getController()).shutdown());
    }

    public static void main(String[] args) {
        launch(args);
    }
}