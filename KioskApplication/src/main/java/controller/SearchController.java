package controller;

import com.jfoenix.controls.JFXComboBox;
import database.objects.Node;
import entity.MapEntity;
import entity.SearchNode;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;
import utility.node.NodeType;

import java.util.function.Predicate;

public class SearchController {

    @FXML
    private JFXComboBox<SearchNode> cbNodes;

    private FilteredList<SearchNode> filteredList;

    private ObservableList<SearchNode> nodeData;

    private SortedList<SearchNode> sortedList;

    private ScreenController parent;

    private TableView<SearchNode> nodeTable;

    public SearchController(ScreenController parent) {
        this.parent = parent;
        nodeData = FXCollections.observableArrayList();
        for(Node databaseNode :MapEntity.getInstance().getAllNodes()) {
            if(databaseNode.getNodeType() != NodeType.HALL) {
                nodeData.add(new SearchNode(databaseNode));
            }
        }
    }

    @FXML
    void initialize() {

        //initialize the lists
        nodeTable = new TableView<>();
        filteredList = new FilteredList<SearchNode>(nodeData, e->true);
        //set the combo box style and editable
        cbNodes.setTooltip(new Tooltip());
        cbNodes.setEditable(true);
        cbNodes.setPromptText("Search by location, doctor or type");
        cbNodes.getEditor().setEditable(true);
        cbNodes.setConverter(new StringConverter<SearchNode>() {
            @Override
            public String toString(SearchNode object) {
                if(object != null) {
                    return object.getSearchString();
                }
                else return null;
            }

            @Override
            public SearchNode fromString(String string) {
                return cbNodes.getItems().stream().filter(searchNode ->
                        searchNode.getSearchString().equals(string)).findFirst().orElse(null);
            }
        });
        /*Don't remove this*/
//        cbNodes.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
//            @Override
//            public ListCell<Node> call(ListView<Node> param) {
//                final ListCell<Node> nodeCell = new ListCell<Node>(){
//                    {
//                        super.setOnMouseEntered(new EventHandler<MouseEvent>() {
//                            @Override
//                            public void handle(MouseEvent event) {
//                                System.out.println("mouse entered");
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Node item, boolean empty) {
//                        System.out.println(item);
//                        super.updateItem(item, empty);
//                    }
//                };
//                return nodeCell;
//            }
//        });
        cbNodes.setOnKeyReleased(e -> {
            cbNodes.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super SearchNode>) searchNode-> {
                    if(newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    else {
                        cbNodes.setPromptText("");
                    }
                    //also check its lower case
                    String lowerCaseFilter = newValue.toLowerCase();
                    //also check its upper case
                    String upperCaseFilter = newValue.toUpperCase();
                    if((searchNode.getDatabaseNode().getNodeID().contains(newValue) || searchNode.getDatabaseNode().getLongName().contains(newValue) || searchNode.getDatabaseNode().getShortName().contains(newValue)) && searchNode.getDatabaseNode().getNodeType() != NodeType.HALL) {
                        return true;
                    }else if((searchNode.getDatabaseNode().getNodeID().contains(lowerCaseFilter) || searchNode.getDatabaseNode().getLongName().contains(lowerCaseFilter) || searchNode.getDatabaseNode().getShortName().contains(lowerCaseFilter)) && searchNode.getDatabaseNode().getNodeType() != NodeType.HALL) {
                        return true;
                    }
                    else if((searchNode.getDatabaseNode().getNodeID().contains(upperCaseFilter) || searchNode.getDatabaseNode().getLongName().contains(upperCaseFilter) || searchNode.getDatabaseNode().getShortName().contains(upperCaseFilter)) && searchNode.getDatabaseNode().getNodeType() != NodeType.HALL) {
                        return true;
                    }
                    return false;
                });
            });
            sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(nodeTable.comparatorProperty());
            nodeTable.setItems(sortedList);
            cbNodes.getItems().clear();
            cbNodes.getItems().addAll(sortedList);
            if(!sortedList.isEmpty()) {
                cbNodes.show();
            }
            else {
                cbNodes.hide();
            }
        });
    }

    @FXML
    void setSearchFieldPromptText(String string) {
        this.cbNodes.getEditor().setPromptText(string);
    }

    public Node getSelected() {
        return cbNodes.getSelectionModel().getSelectedItem().getDatabaseNode();
    }

    public boolean isSelected() {
        return cbNodes.getSelectionModel().isEmpty();
    }


    public ObjectProperty<SearchNode> getCBValueProperty() {
        return cbNodes.valueProperty();
    }

    public void clearSearch() {
        cbNodes.getItems().clear();
        sortedList.clear();
        nodeTable.getItems().clear();
    }
}
