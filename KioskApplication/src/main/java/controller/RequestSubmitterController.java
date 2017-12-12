package controller;

import com.jfoenix.controls.*;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.FoodEntities.*;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import utility.FoodType;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.request.Language;
import utility.request.RequestType;

import java.io.IOException;

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
    @FXML private JFXTreeTableView<FoodMenuItem> menuTable;
    @FXML private JFXTextField deliveryLocation;

    /*security related*/
    @FXML private Tab securityTab;
    @FXML private JFXTextField secLocationField;
    @FXML private JFXComboBox priorityMenu;
    @FXML private JFXTextArea secNoteField;

    /*janitor related*/
    @FXML private Tab janitorTab;
    @FXML private JFXTextField janLocationField;
    @FXML private JFXTextArea janNotesField;

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

        //TODO: is this still necessary?
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

                janNotesField.clear();
                janLocationField.clear();
            }
            resetTimer();
        });


        final TreeItem<FoodMenuItem> entrees = new TreeItem<>(new FoodMenuItem("Entrees", 0.0, FoodType.ENTREE));
        final TreeItem<FoodMenuItem> drinks = new TreeItem<>(new FoodMenuItem("Drinks", 0.0, FoodType.DRINK));
        final TreeItem<FoodMenuItem> sides = new TreeItem<>(new FoodMenuItem("Sides", 0.0, FoodType.SIDE));

        //Creating the root element
        final TreeItem<FoodMenuItem> root = new TreeItem<>(new FoodMenuItem("Menu", 0.0, FoodType.ENTREE));
        root.setExpanded(true);

        root.getChildren().setAll(entrees,drinks,sides);

        restaurantComboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null) return;
            Node restNode = newValue;
            if(restNode.getLongName().toLowerCase().contains("caf")) {
                System.out.println("Cafe reached");
                Cafe menu = new Cafe();
                sides.getChildren().clear();
                entrees.getChildren().clear();
                drinks.getChildren().clear();
                for(FoodMenuItem item: menu.getFoodType(FoodType.SIDE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    sides.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.ENTREE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    entrees.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.DRINK)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    drinks.getChildren().add(fooditem);
                }
            }else if(restNode.getLongName().toLowerCase().contains("vending")){
                System.out.println("Vending reached");
                VendingMachine menu = new VendingMachine();
                sides.getChildren().clear();
                entrees.getChildren().clear();
                drinks.getChildren().clear();
                for(FoodMenuItem item: menu.getMenu()){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    sides.getChildren().add(fooditem);
                }
            }else if(restNode.getLongName().toLowerCase().contains("pain")){
                System.out.println("Au Bon Pain reached");
                AuBonPain menu = new AuBonPain();
                sides.getChildren().clear();
                entrees.getChildren().clear();
                drinks.getChildren().clear();
                for(FoodMenuItem item: menu.getFoodType(FoodType.SIDE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    sides.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.ENTREE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    entrees.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.DRINK)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    drinks.getChildren().add(fooditem);
                }
            }else if(restNode.getLongName().toLowerCase().contains("pat")){
                System.out.println("Pats Place reached");
                PatsPlace menu = new PatsPlace();
                sides.getChildren().clear();
                entrees.getChildren().clear();
                drinks.getChildren().clear();
                for(FoodMenuItem item: menu.getFoodType(FoodType.SIDE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    sides.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.ENTREE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    entrees.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.DRINK)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    drinks.getChildren().add(fooditem);
                }
            }else if(restNode.getLongName().toLowerCase().contains("gift")){
                System.out.println("Gift Shop reached");
                GiftShop menu = new GiftShop();
                sides.getChildren().clear();
                entrees.getChildren().clear();
                drinks.getChildren().clear();
                for(FoodMenuItem item: menu.getFoodType(FoodType.SIDE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    sides.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.ENTREE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    entrees.getChildren().add(fooditem);
                }
                for(FoodMenuItem item: menu.getFoodType(FoodType.DRINK)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    drinks.getChildren().add(fooditem);
                }
            }else {
                System.out.println("No restaurant/restaurant missing");
            }
        });

        //Creating a column
        TreeTableColumn<FoodMenuItem, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setPrefWidth(200);
        nameColumn.setResizable(false);
        TreeTableColumn<FoodMenuItem, String> costColumn = new TreeTableColumn<>("Cost");
        costColumn.setPrefWidth(75);
        costColumn.setResizable(false);
        TreeTableColumn<FoodMenuItem, Boolean> checkboxColumn = new TreeTableColumn<>("Selected");
        checkboxColumn.setPrefWidth(70);
        checkboxColumn.setResizable(false);

        //Defining cell content
        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<FoodMenuItem, String> p) ->
            new ReadOnlyStringWrapper(p.getValue().getValue().getName()));

        costColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<FoodMenuItem, String> p) -> {
            if (p.getValue().isLeaf()) {
                String moneyValue = Double.toString(p.getValue().getValue().getCost());
                if (moneyValue.length() == 2) moneyValue += "0";
                return new ReadOnlyStringWrapper("$" + moneyValue);
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        checkboxColumn.setCellFactory( tc -> new CheckBoxTreeTableCell<>());
        checkboxColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<FoodMenuItem, Boolean> p) -> {
            if (!p.getValue().isLeaf()) {
                return new ReadOnlyBooleanWrapper(false);
            } else {
                return p.getValue().getValue().selectedProperty();
            }
        });

        //Creating a tree table view
        menuTable.setRoot(root);
        menuTable.getColumns().addAll(nameColumn, costColumn, checkboxColumn);
        menuTable.setShowRoot(false);
        menuTable.setEditable(true);
    }

    /**
     * When the submit button is pressed this sends a request to the database
     * @throws IOException
     */
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
            case JANITOR:
                addJanitorRequest();
                break;
        }
    }

    /**
     * Clears all the fields in the request submitter
     */
    @FXML
    public void clearButton() {
        intLocation.setText("");
        intNotesArea.setText("");
        langMenu.setValue(null);

        restaurantComboBox.setValue(null);
        deliveryTimePicker.setValue(null);
        deliveryLocation.setText("");

        secLocationField.setText("");
        secNoteField.setText("");
        priorityMenu.setValue(null);
    }

    /**
     * Adds an interpreter Request to the database
     */
    public void addIntRequest() {
        Language language = Language.valueOf(langMenu.getValue().toString().toUpperCase());
        requestEntity.submitInterpreterRequest(intLocation.getText(), loginEntity.getCurrentLoginID(), intNotesArea.getText(), language);
        System.out.println("location: " + intLocation.getText() + ". language: " + language.toString() + ". Assigner: " + loginEntity.getCurrentLoginID());
        clearButton();
    }

    /**
     * Adds a security Request to the database
     */
    public void addSecRequest() {
        int priority = Integer.parseInt(priorityMenu.getValue().toString());
        System.out.println("location: " + secLocationField.getText() + ". priority: " + priority + ". Admin Email: " + loginEntity.getCurrentLoginID());
        //node ID, employee, notes, priority
        requestEntity.submitSecurityRequest(secLocationField.getText(), loginEntity.getCurrentLoginID(), secNoteField.getText(), priority);
        clearButton();
    }

    /**
     * Adds a food Request to the database
     */
    public void addFoodRequest(){
        String order = "Order:";
//        for (int i = 0; i < menuTable.getCurrentItemsCount(); i++) {
        for(TreeItem<FoodMenuItem> catagories: menuTable.getRoot().getChildren()){
            for(TreeItem<FoodMenuItem> item: catagories.getChildren()){
                String fooditem = item.getValue().getName();
                if (item.isLeaf() && item.getValue().selectedProperty().get() &&
                        !(item.getValue().getName().equals("Drinks") ||
                                item.getValue().getName().equals("Entrees") ||
                                item.getValue().getName().equals("Sides"))){
                    order += " " + item.getValue().getName() + " (" + item.getValue().getCost() + "),";
                }
//            TreeItem<FoodMenuItem> item = menuTable.getTreeItem(i);
            }
        }
        order = order.trim();
        if (order.endsWith(",")) order.substring(0, order.length() - 1);

        requestEntity.submitFoodRequest(deliveryLocation.getText(),loginEntity.getCurrentLoginID(),order,
                restaurantComboBox.getValue().getNodeID(),deliveryTimePicker.getValue());
        System.out.println(requestEntity.getAllFoodRequests());
        clearButton();
    }

    private void addJanitorRequest(){
        requestEntity.submitJanitorRequest(janLocationField.getText(),loginEntity.getCurrentLoginID(),janNotesField.getText());
        clearButton();
    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    public void resetTimer(){
        getParent().resetTimer();
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
                janLocationField.setText(n.getNodeID());
                break;
        }
    }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0,400,0,0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(false);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
    }
}