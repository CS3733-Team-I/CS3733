package FoodRequestAPI.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import FoodRequestAPI.database.connection.NotFoundException;
import FoodRequestAPI.database.objects.Employee;
import FoodRequestAPI.database.objects.Request;
import FoodRequestAPI.entity.LoginEntity;
import FoodRequestAPI.entity.MapEntity;
import FoodRequestAPI.entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import FoodRequestAPI.utility.KioskPermission;
import FoodRequestAPI.utility.ResourceManager;
import FoodRequestAPI.utility.request.RequestProgressStatus;
import FoodRequestAPI.utility.request.RequestType;

public class RequestListCellController {

    LoginEntity lEntity;
    RequestEntity rEntity;
    Request request;
    RequestManagerController parent;

    MapEntity mapEntity;

    @FXML private ImageView iconView,readView;
    @FXML private Label nameLabel,timeLabel,extraField,requestNotes,locationOfRequest,person;
    @FXML private JFXButton locateNodeButton,delete;
    @FXML private HBox buttonBox;
    @FXML private VBox textFlow;
    private VBox rootVbox;


    public RequestListCellController(Request request, RequestManagerController parent) throws NotFoundException {
        lEntity = LoginEntity.getInstance();
        rEntity = RequestEntity.getInstance();
        this.request = request;
        this.parent = parent;
        rootVbox = new VBox();
        mapEntity = MapEntity.getInstance();

        //TODO: should all this below be in initialize function?
        FXMLLoader loader = ResourceManager.getInstance().getFXMLLoader("/view/RequestListCellView.fxml");
        loader.setRoot(rootVbox);
        loader.setController(this);
        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String requestID = request.getRequestID();
        String location = MapEntity.getInstance().getNode(request.getNodeID()).getLongName();
        String assigner = rEntity.getAssigner(requestID).getUsername();
        String completer = rEntity.getCompleter(requestID).getUsername();
        RequestType RT = rEntity.checkRequestType(requestID);
        RequestProgressStatus RPS = request.getStatus();

        nameLabel.setText(RT.toString());
        timeLabel.setText(request.getSubmittedTime().toString());
        person.setText("No One?"); //had to initialize it
        requestNotes.setText(request.getNote());
        locationOfRequest.setText(location);

        Image readIcon = ResourceManager.getInstance().getImage("/images/icons/readIcon.png");
        readView.setImage(readIcon);
        readView.setFitWidth(24);
        readView.setFitHeight(24);

        String restaurantID = rEntity.getFoodRequest(requestID).getDestinationID();
        String restaurant = MapEntity.getInstance().getNode(restaurantID).getLongName();
        extraField.setText("Restaurant: " + restaurant);
        Image foodIcon = ResourceManager.getInstance().getImage("/images/icons/foodIconBlack.png");
        iconView.setImage(foodIcon);
        iconView.setFitHeight(48);
        iconView.setFitWidth(48);

        switch(RPS){
            case TO_DO:
                person.setText("Requested by: " + assigner);
                break;
            case IN_PROGRESS:
                person.setText("Assigned to: " + completer);
                break;
            case DONE:
                person.setText("Completed by: " + completer);
                break;
        }
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                rEntity.deleteRequest(requestID);
                parent.refreshRequests();
            }
        });

        if(!lEntity.getCurrentPermission().equals(KioskPermission.EMPLOYEE)){ //Admin or super
            if(RPS.equals(RequestProgressStatus.TO_DO)) { //and still assignable
                ObservableList<Employee> listOfEmployees = FXCollections.observableArrayList();
                listOfEmployees.clear();
                listOfEmployees.addAll(lEntity.getAllEmployeeType(request));
                JFXComboBox employees = new JFXComboBox(listOfEmployees);
                employees.setCellFactory(new Callback<ListView, ListCell>() {
                    @Override
                    public ListCell call(ListView param) {
                        final ListCell<Employee> cell = new ListCell<Employee>(){
                            @Override
                            public void updateItem(Employee item, boolean empty){
                                super.updateItem(item,empty);
                                if(item != null){
                                    setText(item.getUsername());
                                }
                            }
                        };
                        return cell;
                    }
                });

                employees.setPromptText("Assign Employee");
                employees.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (employees.getValue().equals(null)) {
                        } else {
                            rEntity.markInProgress(( (Employee) employees.getValue()).getID(), requestID);
                            parent.refreshRequests();
                        }
                    }
                });

                buttonBox.getChildren().add(employees);
            }else if(RPS.equals(RequestProgressStatus.IN_PROGRESS)){
                JFXButton statusUpdater = new JFXButton();
                statusUpdater = new JFXButton("Completed");
                statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        rEntity.completeRequest(requestID);
                        parent.refreshRequests();
                    }
                });
                buttonBox.getChildren().add(statusUpdater);
            }
        }
    }

    public VBox getView() {
        return rootVbox;
    }

}