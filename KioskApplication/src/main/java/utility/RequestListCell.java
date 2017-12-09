package utility;

import com.jfoenix.controls.JFXListCell;
import controller.RequestListCellController;
import controller.RequestManagerController;
import database.connection.NotFoundException;
import database.objects.Request;

public final class RequestListCell extends JFXListCell<Request> {

    RequestManagerController parent;


    private RequestListCellController controller;

    public RequestListCell(RequestManagerController parent) {
        this.parent = parent;
    }

    /**
     * Updates the listCell to display all the information about a request
     * Uses RequestListCellController to set all the information
     * @param item the vbox created from RequestListCellController
     * @param empty determines if there is a listCell to be filled
     */
    @Override
    public void updateItem(Request item, boolean empty) {
        super.updateItem(item, empty);
        if(empty){
            setGraphic(null);
        }else{
            try {
                controller = new RequestListCellController(item, parent);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            setText("");
            setPrefWidth(300);
            setGraphic(controller.getView());
        }
    }
}