package controller;

import com.jfoenix.controls.*;
import database.objects.Employee;
import entity.LoginEntity;
import entity.SystemSettings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import utility.KioskPermission;
import utility.request.Language;
import utility.request.RequestType;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class EmployeeSettingsController {

    @FXML private Label employeesLabel;

    @FXML private JFXButton addUserButton;
    @FXML private JFXButton deleteUserButton;

    @FXML private GridPane userEditorPane;
    @FXML private Label registerEmployeeLabel;
    @FXML private Label errLabel;
    @FXML private JFXTextField firstNameBox;
    @FXML private JFXTextField lastNameBox;
    @FXML private JFXTextField usernameBox;
    @FXML private JFXPasswordField passwordBox1;
    @FXML private JFXPasswordField passwordBox2;
    @FXML private JFXComboBox<KioskPermission> permissionSelect;
    @FXML private JFXComboBox<RequestType> typeSelect;
    @FXML private JFXButton userActionButton;
    @FXML private AnchorPane interpreterLanguageContainer;

    @FXML private VBox interpreterLanguageBox;

    @FXML private BorderPane deletePane;
    @FXML private Label deleteText;

    @FXML private JFXTreeTableView<Employee> usersList;
    private final TreeItem<Employee> root = new TreeItem<>();

    Employee selectedEmployee;

    public EmployeeSettingsController(){
    }

    @FXML
    void initialize() {
        root.setExpanded(true);

        TreeTableColumn<Employee, String> usernameColumn = new TreeTableColumn<>("Username");
        usernameColumn.setResizable(false);
        usernameColumn.setPrefWidth(200);
        usernameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getUsername())
        );
        TreeTableColumn<Employee, String> firstNameColumn = new TreeTableColumn<>("First Name");
        firstNameColumn.setResizable(false);
        firstNameColumn.setPrefWidth(175);
        firstNameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getFirstName())
        );
        TreeTableColumn<Employee, String> lastNameColumn = new TreeTableColumn<>("Last Name");
        lastNameColumn.setResizable(false);
        lastNameColumn.setPrefWidth(175);
        lastNameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getLastName())
        );

        TreeTableColumn<Employee, String> permissionColumn = new TreeTableColumn<>("Permission");
        permissionColumn.setResizable(false);
        permissionColumn.setPrefWidth(100);
        permissionColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getPermission().toString())
        );

        TreeTableColumn<Employee, String> serviceColumn = new TreeTableColumn<>("Service Availability");
        serviceColumn.setResizable(false);
        serviceColumn.setPrefWidth(150);
        serviceColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getServiceAbility().toString())
        );
        TreeTableColumn<Employee, String> optionsColumn = new TreeTableColumn<>("Options");
        optionsColumn.setResizable(false);
        optionsColumn.setPrefWidth(150);
        optionsColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getOptions())
        );

        usersList.getColumns().setAll(usernameColumn,firstNameColumn,lastNameColumn, permissionColumn, serviceColumn, optionsColumn);
        usersList.setRoot(root);
        usersList.setShowRoot(false);

        usersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LoginEntity e = LoginEntity.getInstance();
                // Don't allow deletion if the selected user is self
                if (!newValue.getValue().getUsername().equals(e.getCurrentUsername())) {
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
        //adds the language checkboxes to the language selection menu
        for (Language language: Language.values()
             ) {
            if(language!=Language.NONE) {
                JFXCheckBox langCheckBox = new JFXCheckBox(language.toString());
                langCheckBox.setPrefWidth(100.0);
                langCheckBox.setPrefHeight(25.0);
                interpreterLanguageBox.getChildren().add(langCheckBox);
            }
        }
        //Internationalization listener
        SystemSettings.getInstance().addObserver((o, arg) -> {
            ResourceBundle rB = SystemSettings.getInstance().getResourceBundle();
            employeesLabel.setText(rB.getString("employees"));
            usernameColumn.setText(rB.getString("username"));
            firstNameColumn.setText(rB.getString("firstName"));
            lastNameColumn.setText(rB.getString("lastName"));
            permissionColumn.setText(rB.getString("permission"));
            serviceColumn.setText(rB.getString("serviceAbility"));
            optionsColumn.setText(rB.getString("options"));
            usernameBox.setPromptText(rB.getString("username"));
            firstNameBox.setPromptText(rB.getString("firstName"));
            lastNameBox.setPromptText(rB.getString("lastName"));
            passwordBox1.setPromptText(rB.getString("password"));
            passwordBox2.setPromptText(rB.getString("password"));
            permissionSelect.setPromptText(rB.getString("permission"));
            typeSelect.setPromptText(rB.getString("serviceAbility"));
            registerEmployeeLabel.setText(rB.getString("registerEmployee"));
        });
    }

    public void refreshUsers() {
        root.getChildren().clear();
        selectedEmployee = null;

        ArrayList<Employee> logins = LoginEntity.getInstance().getAllLogins();
        logins.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<Employee>(employee));
        });

        deleteUserButton.setDisable(true);
    }

    @FXML
    void onAddPressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userEditorPane.setVisible(true);
        deletePane.setVisible(false);
        addUserButton.setVisible(false);
        deleteUserButton.setVisible(false);
        userActionButton.setText("Add");
        errLabel.setText("");
    }

    @FXML
    void onDeletePressed(ActionEvent event) {
        // Set delete text
        deleteText.setText("Delete " + selectedEmployee.getUsername() + "?");

        // Adjust visability
        usersList.setVisible(false);
        userEditorPane.setVisible(false);
        deletePane.setVisible(true);
    }

    @FXML
    void deleteSelectedUser(ActionEvent even) {
        // Delete user
        LoginEntity.getInstance().deleteLogin(selectedEmployee.getUsername());

        refreshUsers();

        // Adjust visability
        usersList.setVisible(true);
        userEditorPane.setVisible(false);
        deletePane.setVisible(false);
    }

    @FXML
    void onUserCancel(ActionEvent event) {
        closeAddEmployee();
    }

    @FXML
    void onUserSave(ActionEvent event) {
        ArrayList<String> options = new ArrayList<>();
        // Check that all fields are filled in
        if (firstNameBox.getText() != null && lastNameBox.getText() != null &&
                usernameBox.getText() != null && !usernameBox.getText().equals("") && passwordBox1.getText() != null &&
                !passwordBox1.getText().equals("") && permissionSelect.getValue() != null && typeSelect.getValue() != null) {
            switch (typeSelect.getValue()){
                case INTERPRETER:
                    for (Node intLangBoxItem: interpreterLanguageBox.getChildren()) {
                        if(intLangBoxItem instanceof JFXCheckBox) {
                            JFXCheckBox langBox = ((JFXCheckBox) intLangBoxItem);
                            if (langBox.isSelected()) {
                                options.add(langBox.getText());
                            }
                        }
                    }
                    break;
                default:
                    options.add("");
                    break;
                }
            }
            // Add user
            if (LoginEntity.getInstance().addUser(usernameBox.getText(),lastNameBox.getText(),firstNameBox.getText(),
                    passwordBox1.getText(),options, permissionSelect.getValue(), typeSelect.getValue())) {
                refreshUsers();
                // Adjust visability
                closeAddEmployee();
                errLabel.setText("User Added");
                resetScreen();
            }
        else{
            if(usernameBox.getText().equals("")){
                System.out.println("USER ERROR");
                errLabel.setText("Username Required");
            }
            else if(passwordBox1.getText().equals("")){
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

    /**
     * Helper method for closing the add employee menu
     */
    private void closeAddEmployee(){
        usersList.setVisible(true);
        userEditorPane.setVisible(false);
        firstNameBox.clear();
        lastNameBox.clear();
        usernameBox.clear();
        passwordBox1.clear();
        passwordBox2.clear();
        typeSelect.valueProperty().set(null);
        permissionSelect.valueProperty().set(null);
        deletePane.setVisible(false);
        addUserButton.setVisible(true);
        deleteUserButton.setVisible(true);

    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    void resetTimer(){

    }

    void resetScreen(){
        usernameBox.setText("");
        passwordBox1.setText("");
        permissionSelect.setValue(null);
        typeSelect.setValue(null);
    }

    /**
     * Called by the request type JFXComboBox
     * Used currently to show or hide the interpreter language selection area
     */
    @FXML
    public void checkEmployeeType(){
        if(typeSelect.getValue()==RequestType.INTERPRETER){
            interpreterLanguageContainer.setVisible(true);
        }
        else {
            interpreterLanguageContainer.setVisible(false);
        }
    }
}