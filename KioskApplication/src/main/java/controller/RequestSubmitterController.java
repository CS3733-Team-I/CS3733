package controller;

import com.jfoenix.controls.*;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.request.Language;
import utility.request.RequestType;

import java.io.IOException;
import java.time.LocalTime;

public class RequestSubmitterController extends ScreenController {

    @FXML private JFXTabPane requestTypeTabs;
    @FXML private Tab interpreterTab;
    @FXML private JFXTextField intLocation;
    @FXML private JFXComboBox langMenu;
    @FXML private JFXTextArea intNotesArea;

    /*food related*/
    @FXML private Tab foodTab;
    @FXML private JFXComboBox<Node> restaurantComboBox;
    @FXML private JFXTimePicker deliveryTimePicker;
    @FXML private JFXTreeTableView menuTable;
    @FXML private TreeTableColumn<String,String> menuColumn;
    @FXML private JFXTextField deliveryLocation;

    /*security related*/
    @FXML private Tab securityTab;
    @FXML private JFXTextField secLocationField;
    @FXML private JFXComboBox priorityMenu;
    @FXML private JFXTextArea secNoteField;

    /*janitor related*/
    @FXML private Tab janitorTab;

    RequestType currentRequestType = RequestType.INTERPRETER;

    LoginEntity loginEntity;
    RequestEntity requestEntity;

    public RequestSubmitterController(MainWindowController parent, MapController map) {
        super(parent, map);
        loginEntity = LoginEntity.getInstance();
        requestEntity = RequestEntity.getInstance();
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

        ObservableList<Node> restaurants = FXCollections.observableArrayList();
        for (Node node : MapEntity.getInstance().getAllNodes()) {
            if (node.getNodeType() == NodeType.RETL) restaurants.add(node);
        }
        restaurantComboBox.setItems(restaurants);

        restaurantComboBox.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
            @Override
            public ListCell<Node> call(ListView<Node> param) {
                final ListCell<Node> cell = new ListCell<Node>() {
                    @Override
                    public void updateItem(Node item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getLongName());
                        }
                    }
                };
                return cell;
            }
        });

        requestTypeTabs.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == interpreterTab) {
                currentRequestType = RequestType.INTERPRETER;

                intNotesArea.clear();
                intLocation.clear();

                langMenu.setValue(null);
            } else if (newValue == foodTab) {
                currentRequestType = RequestType.FOOD;
            } else if (newValue == securityTab) {
                currentRequestType = RequestType.SECURITY;

                secNoteField.clear();
                secLocationField.clear();

                priorityMenu.setValue(null);
            } else if (newValue == janitorTab) {
                currentRequestType = RequestType.JANITOR;
            }
        });
    }

    @FXML
    public void addRequest() throws IOException{
        switch(currentRequestType){
            case INTERPRETER:
                addIntRequest();
                break;
            case SECURITY:
                addSecRequest();
                break;
            case FOOD:
                addFoodRequest();
                break;
        }
    }

    @FXML
    public void clearButton() {
        intLocation.setText("");
        intNotesArea.setText("");
        langMenu.setValue(null);

        restaurantComboBox.setValue(null);
        deliveryTimePicker.setValue(LocalTime.now());
        deliveryLocation.setText("");

        secLocationField.setText("");
        secNoteField.setText("");
        priorityMenu.setValue(null);
    }

    public void addIntRequest() {
        Language language = Language.valueOf(langMenu.getValue().toString().toUpperCase());
        requestEntity.submitInterpreterRequest(intLocation.getText(), loginEntity.getLoginID(), intNotesArea.getText(), language);
        System.out.println("location: " + intLocation.getText() + ". language: " + language.toString() + ". Assigner: " + loginEntity.getLoginID());
    }

    public void addSecRequest() {
        int priority = Integer.parseInt(priorityMenu.getValue().toString());
        System.out.println("location: " + secLocationField.getText() + ". priority: " + priority + ". Admin Email: " + loginEntity.getLoginID());
        //node ID, employee, notes, priority
        requestEntity.submitSecurityRequest(secLocationField.getText(), loginEntity.getLoginID(), secNoteField.getText(), priority);
    }

    public void addFoodRequest(){
        String notes = "";
        requestEntity.submitFoodRequest(deliveryLocation.getText(),loginEntity.getLoginID(),notes,
                restaurantComboBox.getValue().getNodeID(),deliveryTimePicker.getValue());
        System.out.println(requestEntity.getAllFoodRequests());
        clearButton();
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
                intLocation.setText(n.getNodeID());
                break;
            case SECURITY:
                secLocationField.setText(n.getNodeID());
                break;
            case FOOD:
                deliveryLocation.setText(n.getNodeID());
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