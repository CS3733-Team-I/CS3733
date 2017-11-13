package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
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
