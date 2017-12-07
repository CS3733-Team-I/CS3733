import com.jfoenix.controls.*;
import database.connection.NotFoundException;
import database.objects.Node;
import entity.FoodMenuItem;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import utility.ResourceManager;
import utility.node.NodeType;
import utility.request.Language;
import utility.request.RequestType;

import java.io.IOException;
import java.time.LocalTime;

public class RequestSubmitterController {
    /*food related*/
    @FXML private Tab foodTab;
    @FXML private JFXComboBox<Node> restaurantComboBox;
    @FXML private JFXTimePicker deliveryTimePicker;
    @FXML private JFXTreeTableView menuTable;
    @FXML private JFXTextField deliveryLocation;

    /*API stuff*/
    javafx.scene.Node contentView;
    String restaurantID, destinationID;

    LoginEntity loginEntity;
    RequestEntity requestEntity;

    public RequestSubmitterController(String restaurantID, String destinationID) {
        requestEntity = RequestEntity.getInstance();

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

        try {
            restaurantComboBox.setValue(MapEntity.getInstance().getNode(restaurantID));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        deliveryLocation.setText(destinationID);

        final TreeItem<FoodMenuItem> childNode1 = new TreeItem<>(new FoodMenuItem("Burger", 4.99));
        final TreeItem<FoodMenuItem> childNode2 = new TreeItem<>(new FoodMenuItem("Soda", 1.0));
        final TreeItem<FoodMenuItem> childNode3 = new TreeItem<>(new FoodMenuItem("Fries", 1.99));

        //Creating the root element
        final TreeItem<FoodMenuItem> root = new TreeItem<>(new FoodMenuItem("Burgers", 0.0));
        root.setExpanded(true);

        //Adding tree items to the root
        root.getChildren().setAll(childNode1, childNode2, childNode3);

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
            if (p.getValue().isLeaf()) {
                return new ReadOnlyBooleanWrapper(false);
            } else {
                return p.getValue().getValue().selectedProperty();
            }
        });

        //Creating a tree table view
        menuTable.setRoot(root);
        menuTable.getColumns().addAll(nameColumn, costColumn, checkboxColumn);
        menuTable.setShowRoot(true);
        menuTable.setEditable(true);
    }

    @FXML
    public void addRequest() throws IOException{
        addFoodRequest();
    }

    @FXML
    public void clearButton() {
        restaurantComboBox.setValue(null);
        deliveryTimePicker.setValue(LocalTime.now());
        deliveryLocation.setText("");
    }

    public void addFoodRequest(){

        String order = "Order:";
        for (int i = 0; i < menuTable.getCurrentItemsCount(); i++) {
            TreeItem<FoodMenuItem> item = menuTable.getTreeItem(i);
            if (item.isLeaf()) {
                order += " " + item.getValue().getName() + " (" + item.getValue().getCost() + "),";
            }
        }

        if (order.endsWith(",")) order.substring(0, order.length() - 1);

        requestEntity.submitFoodRequest(deliveryLocation.getText(),loginEntity.getLoginID(),order,
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