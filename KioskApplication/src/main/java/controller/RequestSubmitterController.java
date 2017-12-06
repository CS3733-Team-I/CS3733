package controller;

import com.jfoenix.controls.*;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.request.Language;
import utility.request.RequestType;

import java.io.IOException;

public class RequestSubmitterController extends ScreenController {

    @FXML private JFXTabPane requestTypeTabs;


    @FXML private Tab interpreterTab;
    @FXML private JFXTextField locationTxt;
    @FXML private JFXComboBox reqMenu;
    @FXML private JFXTextArea notesArea;
    @FXML private HBox row1, row2;
    /*food related*/
    @FXML private Tab foodTab;
    @FXML private JFXComboBox foodMenu;
    /*security related*/
    @FXML private Tab securityTab;
    /*janitor related*/
    @FXML private Tab janitorTab;
    @FXML private JFXButton btnSubmit;

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
                notesArea.clear();
                locationTxt.clear();
                reqMenu.setItems(null);
                ObservableList<String> languages = FXCollections.observableArrayList();
                languages.addAll("Spanish", "Chinese", "French", "Tagalog",
                        "Vietnamese", "Korean","German","Arabic","Russian","Italian","Portuguese");
                reqMenu.setPromptText("Select Language");
                reqMenu.setItems(languages);
            } else if (newValue == foodTab) {
//                System.out.println("FOOD");
                currentRequestType = RequestType.FOOD;
            } else if (newValue == securityTab) {
                currentRequestType = RequestType.SECURITY;
                notesArea.clear();
                locationTxt.clear();
                reqMenu.setItems(null);
                ObservableList<String> priorities = FXCollections.observableArrayList();
                priorities.addAll("1","2","3","4","5");
                reqMenu.setPromptText("Select Priority");
                reqMenu.setItems(priorities);
            } else if (newValue == janitorTab) {
                currentRequestType = RequestType.JANITOR;
            }
        });
    }

    // adds the request. TODO: make this generic and able to process any and all requests
    @FXML
    public void addRequest() throws IOException{
        String location = locationTxt.getText();
        int assigner = l.getLoginID();
        String notes = notesArea.getText();
        if(notes==null){
            notes="";
        }
        RequestType type = currentRequestType;
        switch(type){
            case INTERPRETER:
                addIntRequest(location,assigner,notes);
                break;
            case SECURITY:
                addSecRequest(location,assigner,notes);
                break;
        }
        locationTxt.clear();
        notesArea.clear();
        reqMenu.setValue("");
    }

    @FXML
    public void addIntRequest(String location, int assigner, String notes) throws IOException {
        Language language = Language.valueOf(reqMenu.getValue().toString().toUpperCase());
        r.submitInterpreterRequest(location, assigner, notes, language);
        System.out.println("location: " + location + ". language: " + language.toString() + ". Assigner: " + assigner);
    }

    @FXML
    public void clearButton() {
        locationTxt.clear();
        notesArea.clear();
        reqMenu.setValue("");
    }

    @FXML
    public void addSecRequest(String location, int assigner, String notes)throws IOException {

        int priority = Integer.parseInt(reqMenu.getValue().toString());
        System.out.println("location: " + location + ". priority: " + priority + ". Admin Email: " + assigner);
        //node ID, employee, notes, priority
        r.submitSecurityRequest(location, assigner, notes, priority);
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestSubmitterView.fxml");
        }
        return contentView;
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e) { }

    @Override
    public void onMapNodeClicked(Node n) {
        switch (currentRequestType){
            case INTERPRETER:
                locationTxt.setText(n.getNodeID());
                break;
            case SECURITY:
                locationTxt.setText(n.getNodeID());
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
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(false);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
    }
}