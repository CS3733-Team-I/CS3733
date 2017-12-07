package controller;

import com.jfoenix.controls.JFXListCell;
import database.connection.NotFoundException;
import database.objects.FoodRequest;
import database.objects.Node;
import database.objects.Request;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import utility.ResourceManager;

import javax.swing.text.html.ImageView;

public class RequestListCell extends JFXListCell<Request> {

    LoginEntity lEntity;
    RequestEntity rEntity;

    @FXML private ImageView iconView;
    @FXML private Label nameLabel;


    public RequestListCell() {
        lEntity = LoginEntity.getInstance();
        rEntity = RequestEntity.getInstance();
    }

    @Override
    public void updateItem(Request item, boolean empty){
        super.updateItem(item, empty);

        if (item != null) {
            VBox rootVbox = new VBox();

            FXMLLoader loader = ResourceManager.getInstance().getFXMLLoader("/view/RequestListCellView.fxml");
            loader.setRoot(rootVbox);
            loader.setController(this);
            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String nameLabelText = "";
            // TODO set icons for different types in here
            // TODO handle more names in here
            switch (rEntity.checkRequestType(this.getItem().getRequestID())) {
                case FOOD:
                    FoodRequest req = (FoodRequest) this.getItem();
                    try {
                        Node source = MapEntity.getInstance().getNode(req.getNodeID());
                        nameLabelText = "Order at " + source.getLongName();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

            }
            this.nameLabel.setText(nameLabelText);

            this.getChildren().add(rootVbox);
        }
    }
}