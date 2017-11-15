package KioskApplication.controller;

import KioskApplication.entity.Administrator;
import KioskApplication.entity.AdministratorList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    TextField tfEmail;
    @FXML
    PasswordField pfPassword;
    @FXML
    Label errorMsg;

    MainWindowController parent;
    AdministratorList AdminList;

    public LoginController(MainWindowController parent) {
        this.parent = parent;
        this.AdminList = new AdministratorList();
        AdminList.add_administrator(new Administrator("boss@hospital.com", "123"));
    }


    @FXML
    public void OnLoginClicked() throws IOException {

        if(AdminList.validLogin(new Administrator(tfEmail.getText(), pfPassword.getText()))) {
            parent.switchTo(MainWindowController.MainWindowScene.ADMIN);
            parent.adminWindow.curr_admin_email = tfEmail.getText(); //set the admin email field in AdminWindowController
            parent.LoginPopup.getChildren().clear();
            parent.LoginPopup.getChildren().add(parent.switchButton);
        }
        else {
            errorMsg.setText("Invalid Login. ");
        }
    }

    @FXML
    public void OnBackClicked () throws IOException {
        parent. switchTo(MainWindowController.MainWindowScene.PATHFINDING);
        parent.LoginPopup.getChildren().clear();
        parent.LoginPopup.getChildren().add(parent.switchButton);
    }
}
