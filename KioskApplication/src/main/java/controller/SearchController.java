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

    FuzzySearch fuzzySearch;

    public SearchController() {
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
        filteredList = new FilteredList<>(nodeData, event -> true);
        //set the combo box style and editable
        cbNodes.setEditable(true);
        cbNodes.setPromptText("Search by location, doctor or type");
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

                getListView().setMaxWidth(750);
                if(empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item.getNodeIcon());
                    setText(item.getSearchString());
                }
            }
        });

        cbNodes.getEditor().focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                cbNodes.show();
                cbNodes.setPromptText("");
            } else {
                cbNodes.hide();
                cbNodes.setPromptText("Search by location, doctor or type");
            }
        }));

        cbNodes.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> updateSearch(newValue));

        updateSearch("");
        cbNodes.hide();
    }

    private void updateSearch(String searchText) {
        final List<Node> searchResults = new LinkedList<>();
        if (searchText != null && !searchText.equals("")) {
            try {
                searchResults.addAll(FuzzySearch.fuzzySearch(searchText));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        filteredList.setPredicate((Predicate<? super SearchNode>) searchNode -> {
            if(searchText == null || searchText.isEmpty()) {
                return true;
            }

            for (Node node : searchResults) {
                if (node.getNodeID().equalsIgnoreCase(searchNode.getDatabaseNode().getNodeID())) {
                    return true;
                }
            }

            return false;
        });

        sortedList = new SortedList<>(filteredList);

        cbNodes.getItems().clear();
        cbNodes.getItems().addAll(sortedList);
        if(!sortedList.isEmpty()) {
            cbNodes.show();
        }
        else {
            cbNodes.hide();
        }
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
    }
}
