package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import entity.AdministratorList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import utility.ApplicationScreen;

import java.io.IOException;

public class LoginController {

    @FXML private JFXTextField emailField;
    @FXML private JFXPasswordField passwordField;
    @FXML private Label errorLabel;

    private MainWindowController parent;
    private AdministratorList adminList;

    //To get login info, construct a new Login Controller
    public LoginController(MainWindowController parent) {
        this.parent = parent;
        this.adminList = AdministratorList.getInstance();
    }

    @FXML
    protected void initialize() {
        emailField.requestFocus();
    }

    @FXML
    public void onLoginPressed() throws IOException {
        if (adminList.isValidLogin(emailField.getText(), passwordField.getText())) {
            // TODO inform MainWindowController that a login was successful
            parent.switchToScreen(ApplicationScreen.PATHFINDING);
        }
        else {
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid Login.");
        }
    }

    @FXML
    public void onBackPressed() throws IOException {
        parent.switchToScreen(ApplicationScreen.PATHFINDING);
    }
}
