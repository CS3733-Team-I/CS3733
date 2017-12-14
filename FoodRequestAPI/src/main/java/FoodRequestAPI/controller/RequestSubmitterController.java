package FoodRequestAPI.controller;

import com.jfoenix.controls.*;
import FoodRequestAPI.database.objects.Node;
import FoodRequestAPI.entity.FoodEntities.*;
import FoodRequestAPI.entity.LoginEntity;
import FoodRequestAPI.entity.MapEntity;
import FoodRequestAPI.entity.RequestEntity;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import FoodRequestAPI.utility.FoodType;
import FoodRequestAPI.utility.node.NodeType;
import FoodRequestAPI.utility.request.RequestType;

import java.io.IOException;

public class RequestSubmitterController {

    @FXML private JFXComboBox<Node> restaurantComboBox, destinationComboBox;
    @FXML private JFXTimePicker deliveryTimePicker;
    @FXML private JFXTreeTableView<FoodMenuItem> menuTable;
    @FXML private JFXTextField deliveryLocation;

    RequestType currentRequestType = RequestType.INTERPRETER;

    /* API Stuff */
    javafx.scene.Node contentView;
    String restaurantID, destinationID;

    LoginEntity loginEntity;
    RequestEntity requestEntity;

    public RequestSubmitterController(String restaurantID, String destinationID) {
        requestEntity = RequestEntity.getInstance();
        loginEntity = LoginEntity.getInstance();
        this.restaurantID = restaurantID;
        this.destinationID = destinationID;
    }

    @FXML
    public void initialize() {

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


        ObservableList<Node> nodes = FXCollections.observableArrayList();
        for(Node node: MapEntity.getInstance().getAllNodes()){
            if (node.getNodeType() == NodeType.CONF ||
                    node.getNodeType() == NodeType.DEPT ||
                    node.getNodeType() == NodeType.LABS ||
                    node.getNodeType() == NodeType.INFO){
                nodes.add(node);
            }
        }
        destinationComboBox.setItems(nodes);

        destinationComboBox.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
            @Override
            public ListCell<Node> call(ListView<Node> param) {
                ListCell<Node> cell = new ListCell<Node>() {
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

        //TODO: fix this?
//        try {
//            restaurantComboBox.setValue(MapEntity.getInstance().getNode(restaurantID));
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        }

//        deliveryLocation.setText(destinationID);

        final TreeItem<FoodMenuItem> entrees = new TreeItem<>(new FoodMenuItem("Entrees", 0.0, FoodType.ENTREE));
        final TreeItem<FoodMenuItem> drinks = new TreeItem<>(new FoodMenuItem("Drinks", 0.0, FoodType.DRINK));
        final TreeItem<FoodMenuItem> sides = new TreeItem<>(new FoodMenuItem("Sides", 0.0, FoodType.SIDE));

        //Creating the root element
        final TreeItem<FoodMenuItem> root = new TreeItem<>(new FoodMenuItem("Menu", 0.0, FoodType.ENTREE));
        root.setExpanded(true);


        restaurantComboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null) return;
            Node restNode = newValue;
            if(restNode.getLongName().toLowerCase().contains("caf")) {
                System.out.println("Cafe reached");
                Cafe menu = new Cafe();
                root.getChildren().removeAll();
                root.getChildren().setAll(entrees,drinks,sides);
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
                root.getChildren().removeAll();
                root.getChildren().setAll(sides);
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
                root.getChildren().removeAll();
                root.getChildren().setAll(entrees,drinks,sides);
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
                root.getChildren().removeAll();
                root.getChildren().setAll(entrees,drinks,sides);
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
                root.getChildren().removeAll();
                root.getChildren().setAll(drinks,sides);
                GiftShop menu = new GiftShop();
                sides.getChildren().clear();
                entrees.getChildren().clear();
                drinks.getChildren().clear();
                for(FoodMenuItem item: menu.getFoodType(FoodType.SIDE)){
                    TreeItem<FoodMenuItem> fooditem = new TreeItem<>(item);
                    sides.getChildren().add(fooditem);
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
     * When the submit button is pressed this sends a request to the FoodRequestAPI.database
     * @throws IOException
     */
    @FXML
    public void addRequest() throws IOException{
        addFoodRequest();
    }

    /**
     * Clears all the fields in the request submitter
     */
    @FXML
    public void clearButton() {
        restaurantComboBox.setValue(null);
        deliveryTimePicker.setValue(null);
        destinationComboBox.setValue(null);
    }

    /**
     * Adds a food Request to the FoodRequestAPI.database
     */
    public void addFoodRequest(){
        String order = "Order:";
        for(TreeItem<FoodMenuItem> catagories: menuTable.getRoot().getChildren()){
            for(TreeItem<FoodMenuItem> item: catagories.getChildren()){
                String fooditem = item.getValue().getName();
                if (item.isLeaf() && item.getValue().selectedProperty().get() &&
                        !(item.getValue().getName().equals("Drinks") ||
                                item.getValue().getName().equals("Entrees") ||
                                item.getValue().getName().equals("Sides"))){
                    order += " " + item.getValue().getName() + " (" + item.getValue().getCost() + "),";
                }
            }
        }
        order = order.trim();
        if (order.endsWith(",")) order.substring(0, order.length() - 1);

        int loginid = loginEntity.getCurrentLoginID();
        requestEntity.submitFoodRequest(destinationComboBox.getValue().getNodeID(),loginEntity.getCurrentLoginID(),order,
                restaurantComboBox.getValue().getNodeID(),deliveryTimePicker.getValue());
        System.out.println(requestEntity.getAllFoodRequests());
        clearButton();
    }

    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestSubmitterView.fxml");
        }
        return contentView;
    }

    protected javafx.scene.Node loadView(String path) {
        javafx.scene.Node view;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            loader.setController(this);
            view = loader.load();
        } catch (IOException e) {
            System.out.println("Load " + this + "view failed," + " initialize with empty view");
            e.printStackTrace();

            view = new AnchorPane(); // Initialize contentView as an empty view
        }

        return view;
    }

}