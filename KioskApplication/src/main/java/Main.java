import controller.MainWindowController;
import database.utility.DatabaseException;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utility.ResourceManager;
import utility.csv.CsvFileUtil;

import java.io.IOException;

public class Main extends Application {
    private Pane splashLayout;
    private ProgressBar loadProgress;

    private static final int SPLASH_WIDTH = 2096/4;
    private static final int SPLASH_HEIGHT = 419/4;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        ImageView splash = new ImageView(ResourceManager.getInstance().getImage("/images/BWH_logo_rgb_pos.jpg"));
        splash.setFitWidth(SPLASH_WIDTH);
        splash.setFitHeight(SPLASH_HEIGHT);

        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH);

        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress);

        splashLayout.setStyle(
                "-fx-padding: 5; " +
                        "-fx-background-color: aliceblue; " +
                        "-fx-border-width:5; " +
                        "-fx-border-color: darkblue"
        );
        splashLayout.setEffect(new DropShadow());
    }


    private void showMainStage(final Tuple<MainWindowController, Parent> params) {
        Stage mainStage = new Stage(StageStyle.DECORATED);

        Scene mainScene = new Scene(params.y, 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());
        mainStage.setScene(mainScene);
        mainStage.setTitle("Final Iteration");

        // Cleanup
        mainStage.setOnHidden(e -> params.x.shutdown());
        mainStage.show();
        params.x.postDisplaySetup();
    }

    private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
        loadProgress.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);
                initStage.toFront();
                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
                fadeSplash.play();

                initCompletionHandler.complete();
            }
        });

        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setAlwaysOnTop(true);
        initStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Task<Tuple<MainWindowController, Parent>> task = new Task<Tuple<MainWindowController, Parent>>() {
            @Override
            protected Tuple<MainWindowController, Parent> call() throws InterruptedException, DatabaseException, IOException {
                MapEntity.getInstance().readAllFromDatabase();
                if (MapEntity.getInstance().getAllNodes().size() == 0)
                    CsvFileUtil.getInstance().readAllCsvs();

                SystemSettings.getInstance().updateDistance();

                MainWindowController controller = new MainWindowController();
                FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("/view/MainWindowView.fxml"));
                mainWindowLoader.setController(controller);
                mainWindowLoader.load();

                return new Tuple<>(controller, mainWindowLoader.getRoot());
            }
        };

        showSplash(primaryStage, task, () -> this.showMainStage(task.getValue()));
        new Thread(task).start();
    }

    private interface InitCompletionHandler {
        void complete();
    }

    private class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}
