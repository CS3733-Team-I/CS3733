package controller;

import com.jfoenix.controls.*;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import database.objects.Request;
import entity.MapEntity;
import entity.LoginEntity;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ApplicationScreen;
import utility.ResourceManager;
import utility.request.Language;
import utility.node.NodeFloor;
import utility.request.RequestType;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class RequestSubmitterController extends ScreenController {

    @FXML private JFXTabPane requestTypeTabs;

    @FXML private JFXTextField intLocation;
    @FXML private JFXTextField secLocationField;
    @FXML private JFXComboBox priorityMenu;
    @FXML private Tab interpreterTab;
    @FXML private JFXComboBox langMenu;
    @FXML private JFXTextArea intNotesArea;
    /*food related*/
    @FXML private Tab foodTab;
    @FXML private JFXComboBox foodMenu;
    /*security related*/
    @FXML private Tab securityTab;
    @FXML private JFXTextArea secNoteField;
    /*janitor related*/
    @FXML private Tab janitorTab;

    @FXML private JFXButton btnSubmit;

    @FXML private JFXButton btnCancel;
    @FXML private JFXTextField txtLocation;
    @FXML private JFXDatePicker datePicker;
    @FXML private JFXTimePicker timePicker;
    RequestType currentRequestType = RequestType.INTERPRETER;

    LoginEntity l;
    RequestEntity r;

    public RequestSubmitterController(MainWindowController parent, MapController map) {
        super(parent, map);
        l = LoginEntity.getInstance();
        r = RequestEntity.getInstance();
    }

    @FXML
    public void initialize() {
        Image interpreterIcon = ResourceManager.getInstance().getImage("/images/icons/interpreterIcon.png");
        ImageView interpreterIconView = new ImageView(interpreterIcon);
        interpreterIconView.setRotate(90);
        interpreterIconView.setFitHeight(24);
        interpreterIconView.setFitWidth(24);
        interpreterTab.setGraphic(interpreterIconView);

        Image foodIcon = ResourceManager.getInstance().getImage("/images/icons/foodIcon.png");
        ImageView foodIconView = new ImageView(foodIcon);
        foodIconView.setRotate(90);
        foodIconView.setFitHeight(24);
        foodIconView.setFitWidth(24);
        foodTab.setGraphic(foodIconView);

        Image securityIcon = ResourceManager.getInstance().getImage("/images/icons/securityIcon.png");
        ImageView securityIconView = new ImageView(securityIcon);
        securityIconView.setRotate(90);
        securityIconView.setFitHeight(24);
        securityIconView.setFitWidth(24);
        securityTab.setGraphic(securityIconView);

        Image janitorIcon = ResourceManager.getInstance().getImage("/images/icons/janitor.png");
        ImageView janitorIconView = new ImageView(janitorIcon);
        janitorIconView.setRotate(90);
        janitorIconView.setFitHeight(24);
        janitorIconView.setFitWidth(24);
        janitorTab.setGraphic(janitorIconView);

        requestTypeTabs.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == interpreterTab) {
                currentRequestType = RequestType.INTERPRETER;
            } else if (newValue == foodTab) {
//                System.out.println("FOOD");
                currentRequestType = RequestType.FOOD;
            } else if (newValue == securityTab) {
                currentRequestType = RequestType.SECURITY;
            } else if (newValue == janitorTab) {
                currentRequestType = RequestType.JANITOR;
            }
        });
    }

    @FXML
    void onInterpreterPressed() throws IOException {
        getParent().switchToScreen(ApplicationScreen.REQUEST_SUBMITTER);
    }

    // adds the request. TODO: make this generic and able to process any and all requests
    @FXML
    public void addIntRequest() throws IOException {
        String location = intLocation.getText();
        String assigner = l.getUserID();
        String notes = intNotesArea.getText();
        if (notes==null){
            notes="";
        }
        Language language = Language.valueOf(langMenu.getValue().toString().toUpperCase());
        r.submitInterpreterRequest(location, assigner, notes, language);
        System.out.println("location: " + location + ". language: " + language.toString() + ". Assigner: " + assigner);
        intLocation.clear();
        intNotesArea.clear();
        langMenu.setValue("");
    }

    @FXML
    public void intClear() {
        intLocation.clear();
        intNotesArea.clear();
        langMenu.setValue("");
    }

    @FXML
    public void addSecRequest()throws IOException {
        String location = secLocationField.getText();
        String assigner = l.getUserID();
        String notes = secNoteField.getText();
        int priority = Integer.parseInt(priorityMenu.getValue().toString());
        System.out.println("location: " + location + ". priority: " + priority + ". Admin Email: " + assigner);
        //node ID, employee, notes, priority
        r.submitSecurityRequest(location, assigner, notes, priority);
        secLocationField.clear();
        secNoteField.clear();
        priorityMenu.setValue("");
    }

    @FXML
    public void clearSecPressed(){
        secLocationField.setText("");

    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestSubmitterView.fxml");
        }
        return contentView;
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) { }

    @Override
    public void onMapNodeClicked(Node n) {
        switch (currentRequestType){
            case INTERPRETER:
                intLocation.setText(n.getNodeID());
                break;
            case SECURITY:
                secLocationField.setText(n.getNodeID());
                break;
            case FOOD:
                System.out.println("map clicked in Food tab");
                break;
            case JANITOR:
                System.out.println("map clicked in Janitor tab");
                break;
        }
    }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0,235,0,0);
    }
}