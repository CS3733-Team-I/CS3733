import entity.LoginEntity;
import entity.MapEntity;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utility.KioskPermission;
import utility.csv.CsvFileUtil;
import utility.request.RequestType;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MapEntity.getInstance().readAllFromDatabase();
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCSVs();

        if (LoginEntity.getInstance().getAllLogins().size() == 0)
            LoginEntity.getInstance().addUser("defaultuser", "Wilson", "Wong", "maps", KioskPermission.SUPER_USER, RequestType.GENERAL);

        LoginEntity.getInstance().logIn("defaultuser", "maps");

        AnchorPane root = (AnchorPane) new RequestSubmitterController("", "").getContentView();

        primaryStage.setTitle("Food Request Submitter");

        Scene mainScene = new Scene(root, 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) throws Exception {
        MapEntity.getInstance().readAllFromDatabase();
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCSVs();

        if (LoginEntity.getInstance().getAllLogins().size() == 0)
            LoginEntity.getInstance().addUser("defaultuser", "Wilson", "Wong", "maps", KioskPermission.SUPER_USER, RequestType.GENERAL);

        LoginEntity.getInstance().logIn("defaultuser", "maps");

        AnchorPane root = (AnchorPane) new RequestSubmitterController(destNodeID, originNodeID).getContentView();

        Stage primaryStage = new Stage();
        primaryStage.setX(xcoord);
        primaryStage.setY(ycoord);
        primaryStage.setTitle("Food Request Submitter");

        Scene mainScene = new Scene(root, windowWidth, windowLength);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}