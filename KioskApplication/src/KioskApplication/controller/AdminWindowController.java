package KioskApplication.controller;

import KioskApplication.database.objects.Node;
import javafx.beans.DefaultProperty;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.NodeFloor;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class AdminWindowController extends MapWindowController {

    String curr_admin_email;

    public enum SidebarType {
        SIDEBAR_ADD_NODE,
        SIDEBAR_EDIT_NODE,
        SIDEBAR_ADD_EDGE,
        SIDEBAR_DEL_EDGE,
        SIDEBAR_MENU,
        SIDEBAR_INTERPRETER,
        SIDEBAR_VIEWREQUEST
    }

    AdminAddNodeController addNodeController = null;
    AdminEditNodeController editNodeController = null;
    AdminAddEdgeController addEdgeController = null;
    AdminDeleteEdgeController deleteEdgeController = null;
    InterpreterRequestController interpreterRequestController = null;
    RequestManagerController requestManagerController = null;

    SidebarType currentSidebar;

    public AdminWindowController() throws IOException {
        super();
        switchTo(SIDEBAR_MENU);
        curr_admin_email = "";
        getMapController().setParent(this);
        mapFloorChanged(getMapController().getCurrentFloor());
    }

    public void reset(){
        this.curr_admin_email = "";
    }


    public void showNodes(boolean toggle){
        // toggle the nodes (true means show, false means hide)
    }

    public void showEdges(boolean toggle){
        // toggle the edges (true means show, false means hide)
    }

    public void switchTo(SidebarType sidebar) throws IOException {
        FXMLLoader loader = null;

        mapFloorChanged(getMapController().getCurrentFloor());

        switch (sidebar) {
            case SIDEBAR_MENU:
                AdminSidebarController ASC = new AdminSidebarController(this);
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/AdminSidebarView.fxml"));
                loader.setController(ASC);
                break;

            case SIDEBAR_ADD_NODE:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/addNode.fxml"));
                addNodeController = new AdminAddNodeController(this);
                loader.setController(addNodeController);
                break;

            case SIDEBAR_ADD_EDGE:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/addEdge.fxml"));
                addEdgeController = new AdminAddEdgeController(this);
                loader.setController(addEdgeController);
                break;

            case SIDEBAR_DEL_EDGE:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/deleteEdge.fxml"));
                deleteEdgeController = new AdminDeleteEdgeController(this);
                loader.setController(deleteEdgeController);
                break;

            case SIDEBAR_EDIT_NODE:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/editNode.fxml"));
                editNodeController = new AdminEditNodeController(this);
                loader.setController(editNodeController);
                break;

            case SIDEBAR_INTERPRETER:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/InterpreterRequestView.fxml"));
                interpreterRequestController = new InterpreterRequestController(this);
                loader.setController(interpreterRequestController);
                break;

            case SIDEBAR_VIEWREQUEST:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/RequestManagerView.fxml"));
                requestManagerController = new RequestManagerController(this);
                loader.setController(requestManagerController);
                break;
        }

        if(loader != null) {
            currentSidebar = sidebar;

            getSidebarPane().getChildren().clear();
            getSidebarPane().getChildren().add(loader.load());
        }
    }

    @Override
    void mapLocationClicked(double x, double y) {
        System.out.println(String.format("Admin Map Clicked: %f %f\n", x, y));

        if (addNodeController != null) addNodeController.setCoords(x, y);
    }

    @Override
    void mapNodeClicked(Node node) {
        switch (currentSidebar) {
            case SIDEBAR_ADD_NODE:
                break;
            case SIDEBAR_ADD_EDGE:
                if (addEdgeController != null) addEdgeController.onMapNodePressed(node);
                break;
            case SIDEBAR_EDIT_NODE:
                if (editNodeController != null) editNodeController.onMapNodePressed(node);
                break;
        }
    }

    @Override
    void mapFloorChanged(NodeFloor floor) {
        getMapController().clearMap();
        getMapController().drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(floor));
    }
}
