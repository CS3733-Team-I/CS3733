package controller;

import com.jfoenix.controls.*;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.springframework.cglib.core.Local;
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
    @FXML private JFXTextField deliveryLocation;

    /*security related*/
    @FXML private Tab securityTab;
    @FXML private JFXTextField secLocationField;
    @FXML private JFXComboBox priorityMenu;
    @FXML private JFXTextArea secNoteField;

    /*janitor related*/
    @FXML private Tab janitorTab;

//    @FXML private JFXTabPane requestTypeTabs;
//    @FXML private Tab interpreterTab;
//    @FXML private JFXTextField locationTxt;
//    @FXML private JFXComboBox reqMenu;
//    @FXML private JFXTextArea notesArea;
//    @FXML private HBox row1, row2;
//    /*food related*/
//    @FXML private Tab foodTab;
//    @FXML private JFXComboBox foodMenu;
//    /*security related*/
//    @FXML private Tab securityTab;
//    /*janitor related*/
//    @FXML private Tab janitorTab;
//    @FXML private JFXButton btnSubmit;

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

        ObservableList<String> languages = FXCollections.observableArrayList();
        languages.addAll("Spanish", "Chinese", "French", "Tagalog",
                "Vietnamese", "Korean","German","Arabic","Russian","Italian","Portuguese");
        langMenu.setPromptText("Select Language");
        langMenu.setItems(languages);

        ObservableList<String> priorities = FXCollections.observableArrayList();
        priorities.addAll("1","2","3","4","5");
        priorityMenu.setPromptText("Select Priority");
        priorityMenu.setItems(priorities);

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

        JFXCheckBox fries = new JFXCheckBox("Fries");
        JFXCheckBox salad = new JFXCheckBox("Salad");
        JFXCheckBox chips = new JFXCheckBox("Chips");

        TreeItem<VBox> side1 = new TreeItem<>(new VBox(fries));
        TreeItem<VBox> side2 = new TreeItem<>(new VBox(salad));
        TreeItem<VBox> side3 = new TreeItem<>(new VBox(chips));

        TreeItem<VBox> root1 = new TreeItem<>(new VBox(new Label("Sides")));
        root1.setExpanded(true);
        root1.getChildren().setAll(side1,side2,side3);

        JFXCheckBox hamburger = new JFXCheckBox("Hamburger");
        JFXCheckBox blt = new JFXCheckBox("BLT");
        JFXCheckBox chxParm = new JFXCheckBox("Chicken Parm");

        TreeItem<VBox> main1 = new TreeItem<>(new VBox(hamburger));
        TreeItem<VBox> main2 = new TreeItem<>(new VBox(blt));
        TreeItem<VBox> main3 = new TreeItem<>(new VBox(chxParm));

        TreeItem<VBox> root2 = new TreeItem<>(new VBox(new Label("Entree")));
        root2.setExpanded(true);
        root2.getChildren().setAll(main1,main2,main3);

        JFXCheckBox soda = new JFXCheckBox("Soda");
        JFXCheckBox orange = new JFXCheckBox("Orange Juice");
        JFXCheckBox milk = new JFXCheckBox("Milk");

        TreeItem<VBox> drink1 = new TreeItem<>(new VBox(soda));
        TreeItem<VBox> drink2 = new TreeItem<>(new VBox(orange));
        TreeItem<VBox> drink3 = new TreeItem<>(new VBox(milk));

        TreeItem<VBox> root3 = new TreeItem<>(new VBox(new Label("Entree")));
        root3.setExpanded(true);
        root3.getChildren().setAll(drink1,drink2,drink3);


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
        }
    }

    @FXML
    public void clearButton() {
        intLocation.setText("");
        intNotesArea.setText("");
        langMenu.setValue(null);

        restaurantComboBox.setValue(null);
        deliveryTimePicker.setValue(LocalTime.now());

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
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(false);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
    }
}