package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    TextField tfEmail1;
    @FXML
    TextField tfPassword;

    MainWindowController parent;

    public LoginController(MainWindowController parent) {
        this.parent = parent;
    }

    @FXML
    public void OnLoginClicked() throws IOException {
        parent.switchTo(MainWindowController.MainWindowScene.ADMIN);
        parent.LoginPopup.getChildren().clear();
        parent.LoginPopup.getChildren().add(parent.switchButton);
    }

    @FXML
    public void OnBackClicked () throws IOException {
        parent. switchTo(MainWindowController.MainWindowScene.PATHFINDING);
        parent.LoginPopup.getChildren().clear();
        parent.LoginPopup.getChildren().add(parent.switchButton);
    }
}
