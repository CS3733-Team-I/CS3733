package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import database.connection.NotFoundException;
import database.objects.Request;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utility.KioskPermission;
import utility.ResourceManager;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

public class RequestListCellController {

    LoginEntity lEntity;
    RequestEntity rEntity;
    Request request;

    @FXML private ImageView iconView;
    @FXML private Label nameLabel,timeLabel,extraField,requestNotes,locationOfRequest,person;
    @FXML private JFXButton locateNodeButton;
    @FXML private HBox buttonBox;
    @FXML private VBox textFlow;
    private VBox rootVbox;


    public RequestListCellController(Request request) throws NotFoundException {
        lEntity = LoginEntity.getInstance();
        rEntity = RequestEntity.getInstance();
        this.request = request;
        rootVbox = new VBox();

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
        String assigner = rEntity.getAssigner(requestID).getFirstName();
        String completer = rEntity.getCompleter(requestID).getFirstName();
        RequestType RT = rEntity.checkRequestType(requestID);
        RequestProgressStatus RPS = request.getStatus();

        nameLabel.setText(RT.toString());
        timeLabel.setText(request.getSubmittedTime().toString());
        person.setText("No One?");
        requestNotes.setText(request.getNote());
        locationOfRequest.setText(location);
        switch (RT){
            case INTERPRETER:
                String language = rEntity.getInterpreterRequest(requestID).getLanguage().toString();
                extraField.setText("Language: "+language);
                break;
            case FOOD:
                String restaurantID = rEntity.getFoodRequest(requestID).getDestinationID();
                String restaurant = MapEntity.getInstance().getNode(restaurantID).getLongName();
                extraField.setText("Restaurant: " + restaurant);
                break;
            default: //security
                int priority = rEntity.getSecurityRequest(requestID).getPriority();
                extraField.setText("Priority: "+ priority);
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

        JFXButton delete = new JFXButton("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                rEntity.deleteRequest(requestID);
                //TODO: refresh list of requests
            }
        });

        if(!lEntity.getCurrentPermission().equals(KioskPermission.EMPLOYEE) //Admin or super
                && RPS.equals(RequestProgressStatus.TO_DO)) { //and still assignable
            ObservableList<Integer> listOfEmployees = FXCollections.observableArrayList();
            listOfEmployees.clear();
            listOfEmployees.addAll(lEntity.getAllEmployeeType(rEntity.checkRequestType(requestID)));
            JFXComboBox employees = new JFXComboBox(listOfEmployees);
            employees.setPromptText("Assign Employee");
            employees.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(employees.getValue().equals(null)){
                    }else {
                        rEntity.markInProgress((Integer) employees.getValue(),requestID);
                        //TODO: refresh list of requests
                    }
                }
            });

            buttonBox.getChildren().add(employees);
        }else{ //TODO: the buttons aren't being deleted might be fixed
            JFXButton statusUpdater = new JFXButton();
            switch (RPS) {
                case TO_DO:
                    statusUpdater = new JFXButton("Assign Myself");
                    statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            rEntity.markInProgress(lEntity.getLoginID(), requestID);
                            //TODO: refresh
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
                            //TODO: refresh
                        }
                    });
                    buttonBox.getChildren().add(statusUpdater);
                    break;
            }
        }

        buttonBox.getChildren().add(delete);

//        Label typeOfRequest = new Label(rEntity.checkRequestType(requestID).toString());


        /*
        JFXComboBox employees = new JFXComboBox(listOfEmployees);
        employees.setPromptText("Select Employee");

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                rEntity.deleteRequest(requestID);
                popup.hide();
                refreshRequests();
            }
        });

        VBox vbox = new VBox(more);

        if(!l.getCurrentPermission().equals(KioskPermission.EMPLOYEE)){ //Admin or super
            listOfEmployees.clear();
            listOfEmployees.addAll(l.getAllEmployeeType(rEntity.checkRequestType(requestID)));
            switch (currentButton){
                case TO_DO:
                    statusUpdater = new JFXButton("Assign");
                    statusUpdaterEntity.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            rEntity.markInProgress((Integer) employees.getValue(),requestID);
                            refreshRequests();
                            popup.hide();
                        }
                    });
                    vbox.getChildren().addAll(employees, statusUpdater);
                    break;
            }
        }else {
            switch (currentButton) {
                case TO_DO:
                    statusUpdater = new JFXButton("Assign Me");
                    statusUpdaterEntity.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            rEntity.markInProgress(l.getLoginID(), requestID);
                            refreshRequests();
                            popup.hide();
                        }
                    });
                    vbox.getChildren().add(statusUpdater);
                    break;
                case IN_PROGRESS:
                    statusUpdater = new JFXButton("Completed");
                    statusUpdaterEntity.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            rEntity.completeRequest(requestID);
                            refreshRequests();
                            popup.hide();
                        }
                    });
                    vbox.getChildren().add(statusUpdater);
                    break;
            }
        }
         */


//        if (item != null) {
//            String nameLabelText = "";
//            // TODO set icons for different types in here
//            // TODO handle more names in here
//            switch (rEntity.checkRequestType(this.getItem().getRequestID())) {
//                case FOOD:
//                    FoodRequest req = (FoodRequest) this.getItem();
//                    try {
//                        Node source = MapEntity.getInstance().getNode(req.getNodeID());
//                        nameLabelText = "Order at " + source.getLongName();
//                    } catch (NotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//
//            }
////            this.nameLabel.setText(nameLabelText);
////
////            this.getChildren().add(rootVbox);
//        }
    }


    public VBox getView() {
        return rootVbox;
    }

//    public void setRequest(Request request) {
//        this.request = request;
//    }
}