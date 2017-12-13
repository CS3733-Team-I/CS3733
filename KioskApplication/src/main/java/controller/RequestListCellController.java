package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import database.connection.NotFoundException;
import database.objects.Employee;
import database.objects.FoodRequest;
import database.objects.Node;
import database.objects.Request;
import entity.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import utility.KioskPermission;
import utility.ResourceManager;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;
import javafx.scene.image.Image;

import java.util.LinkedList;

public class RequestListCellController {

    LoginEntity lEntity;
    RequestEntity rEntity;
    Request request;
    RequestManagerController parent;

    MapEntity mapEntity;

    @FXML private ImageView iconView,readView;
    @FXML private Label nameLabel,timeLabel,requestNotes,locationOfRequest,person;
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
        requestNotes.setText("Notes: "+request.getNote());
        locationOfRequest.setText(location);

        Image readIcon = ResourceManager.getInstance().getImage("/images/icons/readIcon.png");
        readView.setImage(readIcon);
        readView.setFitWidth(24);
        readView.setFitHeight(24);

        Label extraField = new Label();

        switch (RT){
            case INTERPRETER:
                String language = rEntity.getInterpreterRequest(requestID).getLanguage().toString();
                extraField.setText("Language: "+language);
                textFlow.getChildren().add(0,extraField);
                Image interpreterIcon = ResourceManager.getInstance().getImage("/images/icons/interpreterIconBlack.png");
                iconView.setImage(interpreterIcon);
                iconView.setFitHeight(48);
                iconView.setFitWidth(48);
                break;
            case FOOD:
                String restaurantID = rEntity.getFoodRequest(requestID).getRestaurantID();
                String restaurant = MapEntity.getInstance().getNode(restaurantID).getLongName();
                extraField.setText("Restaurant: " + restaurant);
                textFlow.getChildren().add(0,extraField);
                Image foodIcon = ResourceManager.getInstance().getImage("/images/icons/foodIconBlack.png");
                iconView.setImage(foodIcon);
                iconView.setFitHeight(48);
                iconView.setFitWidth(48);
                break;
            case JANITOR:
                Image janitorIcon = ResourceManager.getInstance().getImage("/images/icons/janitorBlack.png");
                iconView.setImage(janitorIcon);
                iconView.setFitHeight(48);
                iconView.setFitWidth(48);
                break;
            case IT:
                break;
            case MAINTENANCE:
                break;
            case SECURITY:
                int priority = rEntity.getSecurityRequest(requestID).getPriority();
                extraField.setText("Priority: "+ priority);
                textFlow.getChildren().add(0,extraField);
                Image securityIcon = ResourceManager.getInstance().getImage("/images/icons/securityIconBlack.png");
                iconView.setImage(securityIcon);
                iconView.setFitHeight(48);
                iconView.setFitWidth(48);
                break;
        }
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
            }
        }else{
            JFXButton statusUpdater = new JFXButton();
            switch (RPS) {
                case TO_DO:
                    statusUpdater = new JFXButton("Assign Myself");
                    statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            rEntity.markInProgress(lEntity.getCurrentLoginID(), requestID);
                            parent.refreshRequests();
                        }
                    });
                    buttonBox.getChildren().add(statusUpdater);
                    break;
                case IN_PROGRESS:
                    statusUpdater = new JFXButton("Completed");
                    statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            rEntity.completeRequest(requestID);
                            parent.refreshRequests();
                        }
                    });
                    buttonBox.getChildren().add(statusUpdater);
                    break;
            }
        }
    }

    public void goToLocation(){
        LinkedList<Node> location = new LinkedList<>();
        this.parent.getMapController().clearMap();
        parent.clearPathsButton();

        if(request.getRequestType().equals(RequestType.FOOD)){
            Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());
            try {
                Node restaurant = mapEntity.getNode(((FoodRequest) request).getRestaurantID());
                Node destination = mapEntity.getNode(request.getNodeID());
                this.parent.getMapController().addWaypoint(restaurant);
                location.addFirst(restaurant);
                this.parent.getMapController().addWaypoint(destination);
                location.addLast(destination);
                Path path = pathfinder.generatePath(location);
                this.parent.getMapController().setPath(path);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            catch (PathfinderException e) {
                e.printStackTrace();
            }

        }else{
            try {
                location.add(mapEntity.getNode(request.getNodeID()));
            } catch (NotFoundException e) {
                e.printStackTrace();
            }

            this.parent.getMapController().zoomOnSelectedNodes(location);
        }

        this.parent.getMapController().zoomOnSelectedNodes(location);
    }


    public VBox getView() {
        return rootVbox;
    }

}