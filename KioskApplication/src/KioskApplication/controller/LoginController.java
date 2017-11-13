package KioskApplication.controller;

import KioskApplication.entity.Administrator;
import KioskApplication.entity.AdministratorList;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    TextField tfEmail;
    @FXML
    PasswordField pfPassword;

    MainWindowController parent;

    public LoginController(MainWindowController parent) {
        this.parent = parent;
    }

    @FXML
    public void OnLoginClicked() throws IOException {
        /*up to change*/
        AdministratorList AdminList = new AdministratorList();
        AdminList.add_adminstrator(new Administrator("boss@hospital.com", "imtheboss"));
        if(AdminList.validLogin(new Administrator(tfEmail.getText(), pfPassword.getText()))) {
            parent.switchTo(MainWindowController.MainWindowScene.ADMIN);
            parent.LoginPopup.getChildren().clear();
            parent.LoginPopup.getChildren().add(parent.switchButton);
        }
        else {

        }
    }

    @FXML
    public void OnBackClicked () throws IOException {
        parent. switchTo(MainWindowController.MainWindowScene.PATHFINDING);
        parent.LoginPopup.getChildren().clear();
        parent.LoginPopup.getChildren().add(parent.switchButton);
    }
}
