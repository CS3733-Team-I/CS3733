package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserSettingsController {

    @FXML private JFXButton addUserButton;
    @FXML private JFXButton deleteUserButton;
    @FXML private JFXButton editUserButton;

    @FXML private JFXTextField usernameBox;
    @FXML private JFXTextField passwordBox;

    @FXML private JFXComboBox<?> permissionSelect;
    @FXML private JFXComboBox<?> typeSelect;
    @FXML private JFXButton userActionButton;
    @FXML private Label userDialogLabel;

    @FXML private JFXTreeTableView<?> usersList;

    @FXML
    void onAddPressed(ActionEvent event) {

    }

    @FXML
    void onDeletePressed(ActionEvent event) {

    }

    @FXML
    void onEditPressed(ActionEvent event) {

    }

    @FXML
    void onUserCancel(ActionEvent event) {

    }

    @FXML
    void onUserSave(ActionEvent event) {

    }
}