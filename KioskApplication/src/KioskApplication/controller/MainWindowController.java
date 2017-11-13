package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainWindowController {

    @FXML Button switchButton;
    @FXML Pane contentWindow;
    @FXML AnchorPane LoginPopup;

    enum MainWindowScene {
        PATHFINDING,
        ADMIN
    }

    MainWindowScene currentView = MainWindowScene.PATHFINDING;

    public MainWindowController() {
        // TODO: Move MapEntity population to somewhere that makes sense, this is for testing
        MapEntity mapEntity = MapEntity.getInstance();
        // TODO remove this, for testing purposes
        Node node1 = new Node("Node1", 1234, 1234, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node2 = new Node("Node2", 1234, 1334, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node3 = new Node("Node3", 1334, 1334, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node4 = new Node("Node4", 1334, 1234, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node5 = new Node("Node5", 1434, 1134, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");

        MapEntity.getInstance().addNode(node1);
        MapEntity.getInstance().addNode(node2);
        MapEntity.getInstance().addNode(node3);
        MapEntity.getInstance().addNode(node4);
        MapEntity.getInstance().addNode(node5);

        MapEntity.getInstance().addEdge(new Edge("E1_2", node1.getNodeID(), node2.getNodeID()));
        MapEntity.getInstance().addEdge(new Edge("E2_3", node2.getNodeID(), node3.getNodeID()));
        MapEntity.getInstance().addEdge(new Edge("E3_4", node3.getNodeID(), node4.getNodeID()));
        MapEntity.getInstance().addEdge(new Edge("E4_5", node4.getNodeID(), node5.getNodeID()));
    }

    void switchTo(MainWindowScene scene) throws IOException{
        switch (scene) {
            case ADMIN:
                currentView = MainWindowScene.ADMIN;
                contentWindow.getChildren().setAll(new AdminWindowController());
                switchButton.setText("Logoff");
                switchButton.requestFocus();
                break;

            case PATHFINDING:
                currentView = MainWindowScene.PATHFINDING;
                contentWindow.getChildren().setAll(new PathfindingWindowController());
                switchButton.setText("Admin Login");
                switchButton.requestFocus();
                break;
        }
    }

    @FXML
    protected void initialize() throws IOException
    {
        switchButton.requestFocus(); //redirect cursor on switchButton
        this.switchTo(MainWindowScene.PATHFINDING);
    }

    @FXML
    private void Login() throws IOException{
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/AdminLoginWindow.fxml"));
        loader.setController(new LoginController(this));
        LoginPopup.getChildren().clear();
        LoginPopup.getChildren().add(loader.load());
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (currentView) {
            case ADMIN:
                this.switchTo(MainWindowScene.PATHFINDING);
                break;
            case PATHFINDING:
                this.Login();
                break;
        }
    }
}
