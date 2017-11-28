package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import database.objects.Employee;
import entity.LoginEntity;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.BorderPane;
import utility.KioskPermission;
import utility.request.RequestType;

import java.util.ArrayList;

public class UserSettingsController {

    @FXML private JFXButton addUserButton;
    @FXML private JFXButton deleteUserButton;
    @FXML private JFXButton editUserButton;

    @FXML private JFXTextField usernameBox;
    @FXML private JFXTextField passwordBox;

    @FXML private JFXComboBox<KioskPermission> permissionSelect;
    @FXML private JFXComboBox<RequestType> typeSelect;
    @FXML private JFXButton userActionButton;
    @FXML private Label userDialogLabel;

    @FXML private JFXTreeTableView<Employee> usersList;
    private final TreeItem<Employee> root = new TreeItem<>();

    @FXML private BorderPane userEditorPane;

    Employee selectedEmployee;

    @FXML
    void initialize() {
        userEditorPane.setVisible(false);

        root.setExpanded(true);

        TreeTableColumn<Employee, String> usernameColumn = new TreeTableColumn<>("Username");
        usernameColumn.setResizable(false);
        usernameColumn.setPrefWidth(175);
        usernameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getUserName())
        );

        TreeTableColumn<Employee, String> permissionColumn = new TreeTableColumn<>("Permission");
        permissionColumn.setResizable(false);
        permissionColumn.setPrefWidth(150);
        permissionColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getPermission().toString())
        );

        TreeTableColumn<Employee, String> serviceColumn = new TreeTableColumn<>("Service Availability");
        serviceColumn.setResizable(false);
        serviceColumn.setPrefWidth(175);
        serviceColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getServiceAbility().toString())
        );

        usersList.getColumns().setAll(usernameColumn, permissionColumn, serviceColumn);
        usersList.setRoot(root);
        usersList.setShowRoot(false);

        usersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editUserButton.setDisable(false);
            deleteUserButton.setDisable(false);

            selectedEmployee = newValue.getValue();
        });

        refreshUsers();

        // disable buttons on default
        editUserButton.setDisable(true);
        deleteUserButton.setDisable(true);

        //add items into the combobox
        permissionSelect.getItems().addAll(KioskPermission.values());
        permissionSelect.getItems().remove(KioskPermission.NONEMPLOYEE);    // Except NONEMPLOYEE
        typeSelect.getItems().addAll(RequestType.values());
    }

    private void refreshUsers() {
        root.getChildren().clear();

        ArrayList<Employee> logins = LoginEntity.getInstance().getAllLogins();
        logins.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<>(employee));
        });
    }

    @FXML
    void onAddPressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userEditorPane.setVisible(true);
        userActionButton.setText("Add");
        userDialogLabel.setText("Add User");

    }

    @FXML
    void onDeletePressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userEditorPane.setVisible(true);
        userActionButton.setText("Delete");
        userDialogLabel.setText("Delete User");

    }

    @FXML
    void onEditPressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userEditorPane.setVisible(true);
        userActionButton.setText("Edit");
        userDialogLabel.setText("Edit User");

    }

    @FXML
    void onUserCancel(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(true);
        userEditorPane.setVisible(false);

    }

    @FXML
    void onUserSave(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(true);
        userEditorPane.setVisible(false);

        // Add/edit/delete from database

        // Check that all fields are filled in
        if (usernameBox.getText() != null && passwordBox.getText() != null && permissionSelect.getValue() != null && typeSelect.getValue() != null) {
            // Add
            if (userActionButton.getText().equals("Add")) {

                // Add user
                if (LoginEntity.getInstance().addUser(usernameBox.getText(), passwordBox.getText(), permissionSelect.getValue(), typeSelect.getValue())) {

                    System.out.println("Adding user ... ");
                    System.out.println("User: " + usernameBox.getText());
                    System.out.println("Pass: " + passwordBox.getText());
                    System.out.println("Permission: " + permissionSelect.getValue().toString());
                    System.out.println("Type: " + typeSelect.getValue().toString());
                }
            }
        }

    }
}