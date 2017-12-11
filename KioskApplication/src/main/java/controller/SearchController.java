package controller;

import com.jfoenix.controls.JFXComboBox;
import database.objects.Node;
import entity.MapEntity;
import entity.SearchNode;
import entity.SystemSettings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;
import pathfinder.FuzzySearch;
import utility.node.NodeType;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class SearchController {

    @FXML
    private JFXComboBox<SearchNode> cbNodes;

    private FilteredList<SearchNode> filteredList;

    private ObservableList<SearchNode> nodeData;

    private SortedList<SearchNode> sortedList;

    private ScreenController parent;

    private TableView<SearchNode> nodeTable;
    FuzzySearch fuzzySearch;
    String value;

    public SearchController(ScreenController parent) {
        this.parent = parent;
        nodeData = FXCollections.observableArrayList();
        SystemSettings.getInstance().updateDistance();
        for(Node databaseNode :MapEntity.getInstance().getAllNodes()) {
            if(databaseNode.getNodeType() != NodeType.HALL) {
                nodeData.add(new SearchNode(databaseNode));
            }
        }
    }

    @FXML
    void initialize() {

        //initialize the lists
        fuzzySearch = new FuzzySearch();
        nodeTable = new TableView<>();
        filteredList = new FilteredList<>(nodeData, e->true);
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

        //DONT REMOVE THIS
        cbNodes.setCellFactory(listView -> new ListCell<SearchNode>() {
            @Override
            protected void updateItem(SearchNode item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty) {
                    setGraphic(null);
                }
                else {
                    setGraphic(item.getNodeIcon());
                    setText(item.getSearchString());
                }
            }
        });

        cbNodes.setButtonCell(cbNodes.getCellFactory().call(null));

        cbNodes.setOnKeyReleased(e -> {
            cbNodes.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
                final List<Node> searchResults = new LinkedList<>();;
                if (newValue != null && !newValue.equals("")) {
                    try {
                        searchResults.addAll(FuzzySearch.fuzzySearch(newValue));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                filteredList.setPredicate((Predicate<? super SearchNode>) searchNode-> {
                    if(newValue == null || newValue.isEmpty()) {
                        return true;
                    } else {
                        cbNodes.setPromptText("");
                    }

                    /*//also check its lower case
                    String lowerCaseFilter = newValue.toLowerCase();
                    //also check its upper case
                    String upperCaseFilter = newValue.toUpperCase();
                    value = newValue;
                    if((searchNode.getDatabaseNode().getNodeID().contains(newValue) || searchNode.getDatabaseNode().getLongName().contains(newValue) || searchNode.getDatabaseNode().getShortName().contains(newValue)) && searchNode.getDatabaseNode().getNodeType() != NodeType.HALL) {
                        return true;
                    }else if((searchNode.getDatabaseNode().getNodeID().contains(lowerCaseFilter) || searchNode.getDatabaseNode().getLongName().contains(lowerCaseFilter) || searchNode.getDatabaseNode().getShortName().contains(lowerCaseFilter)) && searchNode.getDatabaseNode().getNodeType() != NodeType.HALL) {
                        return true;
                    }
                    else if((searchNode.getDatabaseNode().getNodeID().contains(upperCaseFilter) || searchNode.getDatabaseNode().getLongName().contains(upperCaseFilter) || searchNode.getDatabaseNode().getShortName().contains(upperCaseFilter)) && searchNode.getDatabaseNode().getNodeType() != NodeType.HALL) {
                        return true;
                    }*/

                    for (Node node : searchResults) {
                        if (node.getNodeID().equalsIgnoreCase(searchNode.getDatabaseNode().getNodeID())) {
                            return true;
                        }
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

    public void reset() {
        nodeData.clear();
        for(Node databaseNode :MapEntity.getInstance().getAllNodes()) {
            if(databaseNode.getNodeType() != NodeType.HALL) {
                nodeData.add(new SearchNode(databaseNode));
            }
        }
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
