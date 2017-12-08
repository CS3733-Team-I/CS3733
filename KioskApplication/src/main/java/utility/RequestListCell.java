package utility;

import com.jfoenix.controls.JFXListCell;
import controller.RequestListCellController;
import database.connection.NotFoundException;
import database.objects.Request;
import entity.LoginEntity;
import entity.RequestEntity;
import javafx.scene.layout.VBox;

public final class RequestListCell extends JFXListCell<Request> {

    LoginEntity lEntity;
    RequestEntity rEntity;

    private RequestListCellController controller;

    @Override
    public void updateItem(Request item, boolean empty) {
        super.updateItem(item, empty);
        if(empty){
            setGraphic(null);
        }else{
            try {
                controller = new RequestListCellController(item);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            setGraphic(controller.getView());
        }
    }


    /*
    public final class ConversationCell<Message> extends ListCell<Message> {

    private final ConversationCellController ccc = new ConversationCellController(null);
    private final Node view = ccc.getView();

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            ccc.setItem(item);
            setGraphic(view);
        }
    }
}
     */
}