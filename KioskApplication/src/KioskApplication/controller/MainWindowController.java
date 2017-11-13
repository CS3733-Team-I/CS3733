package KioskApplication.controller;

import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainWindowController {

    @FXML Button switchButton;
    @FXML Pane contentWindow;
    @FXML AnchorPane LoginPopup;

    enum MainWindowScene {
        PATHFINDING,
        ADMIN
    }

    MainWindowScene currentView = MainWindowScene.PATHFINDING;

    public MainWindowController() {
        // TODO: Move MapEntity population to somewhere that makes sense, this is for testing
        MapEntity mapEntity = MapEntity.getInstance();
        mapEntity.addNode(new Node("a", 2648, 1072, "3", "", "", "", "", "I"));
        mapEntity.addNode(new Node("b", 2769, 1076, "3", "", "", "", "", "I"));
        mapEntity.addNode(new Node("c", 2769, 1282, "3", "", "", "", "", "I"));
    }

    void switchTo(MainWindowScene scene) throws IOException{
        switch (scene) {
            case ADMIN:
                currentView = MainWindowScene.ADMIN;
                contentWindow.getChildren().setAll(new AdminWindowController());
                switchButton.setText("Logoff");
                switchButton.requestFocus();
                break;

            case PATHFINDING:
                currentView = MainWindowScene.PATHFINDING;
                contentWindow.getChildren().setAll(new PathfindingWindowController());
                switchButton.setText("Admin Login");
                switchButton.requestFocus();
                break;
        }
    }

    @FXML
    protected void initialize() throws IOException
    {
        switchButton.requestFocus(); //redirect cursor on switchButton
        this.switchTo(MainWindowScene.PATHFINDING);
    }

    @FXML
    private void Login() throws IOException{
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/AdminLoginWindow.fxml"));
        loader.setController(new LoginController(this));
        LoginPopup.getChildren().clear();
        LoginPopup.getChildren().add(loader.load());
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (currentView) {
            case ADMIN:
                this.switchTo(MainWindowScene.PATHFINDING);
                break;
            case PATHFINDING:
                this.Login();
                break;
        }
    }
}
