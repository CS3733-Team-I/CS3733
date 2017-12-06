import com.jfoenix.controls.*;
import database.connection.NotFoundException;
import database.objects.Node;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeTableColumn;
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
    @FXML private TreeTableColumn<String,String> menuColumn;
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
        String notes = "";
        requestEntity.submitFoodRequest(deliveryLocation.getText(),loginEntity.getLoginID(),notes,
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