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

public class EmployeeSettingsController {

    @FXML private JFXButton addUserButton;
    @FXML private JFXButton deleteUserButton;

    @FXML private BorderPane userEditorPane;
    @FXML private Label userDialogLabel;
    @FXML private Label errLabel;
    @FXML private JFXTextField usernameBox;
    @FXML private JFXTextField passwordBox;
    @FXML private JFXComboBox<KioskPermission> permissionSelect;
    @FXML private JFXComboBox<RequestType> typeSelect;
    @FXML private JFXButton userActionButton;

    @FXML private BorderPane deletePane;
    @FXML private Label deleteText;

    @FXML private JFXTreeTableView<Employee> usersList;
    private final TreeItem<Employee> root = new TreeItem<>();

    Employee selectedEmployee;

    @FXML
    void initialize() {
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
            if (newValue != null) {
                LoginEntity e = LoginEntity.getInstance();
                // Don't allow deletion if the selected user is self
                if (!newValue.getValue().getUserName().equals(e.getUserName())) {
                    deleteUserButton.setDisable(false);
                    selectedEmployee = newValue.getValue();
                }
            }
        });

        refreshUsers();

        errLabel.setText("");
        //add items into the combobox
        permissionSelect.getItems().addAll(KioskPermission.values());
        permissionSelect.getItems().remove(KioskPermission.NONEMPLOYEE); // Except NONEMPLOYEE
        typeSelect.getItems().addAll(RequestType.values());
    }

    private void refreshUsers() {
        root.getChildren().clear();
        selectedEmployee = null;

        ArrayList<Employee> logins = LoginEntity.getInstance().getAllLogins();
        logins.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<>(employee));
        });

        deleteUserButton.setDisable(true);
    }

    @FXML
    void onAddPressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userEditorPane.setVisible(true);
        deletePane.setVisible(false);
        userActionButton.setText("Add");
        userDialogLabel.setText("Add User");
    }

    @FXML
    void onDeletePressed(ActionEvent event) {
        // Set delete text
        deleteText.setText("Delete " + selectedEmployee.getUserName() + "?");

        // Adjust visability
        usersList.setVisible(false);
        userEditorPane.setVisible(false);
        deletePane.setVisible(true);
    }

    @FXML
    void deleteSelectedUser(ActionEvent even) {
        // Delete user
        LoginEntity.getInstance().deleteLogin(selectedEmployee.getUserName());

        refreshUsers();

        // Adjust visability
        usersList.setVisible(true);
        userEditorPane.setVisible(false);
        deletePane.setVisible(false);
    }

    @FXML
    void onUserCancel(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(true);
        userEditorPane.setVisible(false);
        deletePane.setVisible(false);
    }

    @FXML
    void onUserSave(ActionEvent event) {

        // Check that all fields are filled in
        if (usernameBox.getText() != null && !usernameBox.getText().equals("") && passwordBox.getText() != null && !passwordBox.getText().equals("") && permissionSelect.getValue() != null && typeSelect.getValue() != null) {
            // Add user
            if (LoginEntity.getInstance().addUser(usernameBox.getText(), passwordBox.getText(), permissionSelect.getValue(), typeSelect.getValue())) {
                System.out.println("Adding user ... ");
                System.out.println("User: " + usernameBox.getText());
                System.out.println("Pass: " + passwordBox.getText());
                System.out.println("Permission: " + permissionSelect.getValue().toString());
                System.out.println("Type: " + typeSelect.getValue().toString());

                refreshUsers();
                // Adjust visability
                usersList.setVisible(true);
                userEditorPane.setVisible(false);
                deletePane.setVisible(false);
                errLabel.setText("User Added");
            }
        }
        else{
            if(usernameBox.getText().equals("")){
                System.out.println("USER ERROR");
                errLabel.setText("Username Required");
            }
            else if(passwordBox.getText().equals("")){
                errLabel.setText("Password Required");
            }
            else if(permissionSelect.getValue() == null){
                errLabel.setText("Permission Selection Required");
            }
            else if(typeSelect.getValue() == null){
                errLabel.setText("User Type Select Required");
            }
        }
    }
}