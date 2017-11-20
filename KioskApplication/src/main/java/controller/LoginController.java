package controller;

import entity.Administrator;
import entity.AdministratorList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utility.ApplicationScreen;

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

    //To get login info, construct a new Login Controller
    public LoginController(MainWindowController parent) {
        this.parent = parent;
        this.AdminList = new AdministratorList();
        AdminList.add_administrator(new Administrator("boss@hospital.com", "123"));
    }


    @FXML
    public void OnLoginClicked() throws IOException {

        if(AdminList.validLogin(new Administrator(tfEmail.getText(), pfPassword.getText()))) {
            parent.switchToScreen(ApplicationScreen.ADMIN_MENU);
            // TODO replace this
            // parent.adminWindow.curr_admin_email = tfEmail.getText(); //set the admin email field in AdminWindowController
            parent.loginPopup.getChildren().clear();
            parent.loginPopup.getChildren().add(parent.switchButton);
            parent.lbAdminInfo.setText("Logged in as" + tfEmail.getText());
            //parent.serviceTab.setDisable(false);
            //parent.managerTab.setDisable(false);
            //parent.builderTab.setDisable(false);
        }
        else {
            errorMsg.setText("Invalid Login. ");
        }
    }

    @FXML
    public void OnBackClicked () throws IOException {
        parent.switchToScreen(ApplicationScreen.PATHFINDING);
        parent.loginPopup.getChildren().clear();
        parent.loginPopup.getChildren().add(parent.switchButton);
    }
}
