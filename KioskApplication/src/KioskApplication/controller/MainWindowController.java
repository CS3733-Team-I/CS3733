package KioskApplication.controller;

import KioskApplication.entity.Administrator;
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

    AdminWindowController adminWindow;
    PathfindingWindowController pathfindingWindow;

    public MainWindowController() {}

    void switchTo(MainWindowScene scene) throws IOException{
        switch (scene) {
            case ADMIN:
                currentView = MainWindowScene.ADMIN;
                contentWindow.getChildren().setAll(adminWindow);
                switchButton.setText("Logoff");
                switchButton.requestFocus();
                break;

            case PATHFINDING:
                currentView = MainWindowScene.PATHFINDING;
                contentWindow.getChildren().setAll(pathfindingWindow);
                switchButton.setText("Admin Login");
                switchButton.requestFocus();
                break;
        }
    }

    @FXML
    protected void initialize() throws IOException
    {
        // Initialize admin window and pathfinding window
        adminWindow = new AdminWindowController();
        pathfindingWindow = new PathfindingWindowController();

        this.switchTo(MainWindowScene.PATHFINDING);
    }

    @FXML
    private void Login() throws IOException{
        LoginController LC = new LoginController(this);
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/AdminLoginWindow.fxml"));
        loader.setController(LC);
        LoginPopup.getChildren().clear();
        LoginPopup.getChildren().add(loader.load());
        LC.tfEmail.requestFocus();
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (currentView) {
            case ADMIN:
                this.switchTo(MainWindowScene.PATHFINDING);
                this.adminWindow.reset();
                break;
            case PATHFINDING:
                this.Login();
                break;
        }
    }
}
