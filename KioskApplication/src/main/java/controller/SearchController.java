package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.objects.Node;
import entity.MapEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;

import java.util.function.Predicate;

public class SearchController {

    @FXML
    private JFXTextField searchField;
    @FXML
    private JFXComboBox<Node> cbNodes;

    private FilteredList<Node> filteredList;

    private ObservableList<Node> nodeData;

    private ScreenController parent;

    private TableView<Node> nodeTable;

    public SearchController(ScreenController parent) {
        this.parent = parent;
        nodeData = FXCollections.observableArrayList(MapEntity.getInstance().getAllNodes());
    }

    @FXML
    void initialize() {
        nodeTable = new TableView<>();
        filteredList = new FilteredList<Node>(nodeData, e->true);
        searchField.setOnKeyReleased(e -> {
            searchField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Node>) node-> {
                    if(newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    //also check its lower case
                    String lowerCaseFilter = newValue.toLowerCase();
                    if(node.getNodeID().contains(newValue) || node.getLongName().contains(newValue) || node.getShortName().contains(newValue)
                            || node.getNodeType().toString().contains(newValue)) {
                        return true;
                    }else if(node.getNodeID().contains(lowerCaseFilter) || node.getLongName().contains(lowerCaseFilter) || node.getShortName().contains(lowerCaseFilter)
                            || node.getNodeType().toString().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Node> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(nodeTable.comparatorProperty());
            nodeTable.setItems(sortedList);
            cbNodes.getItems().clear();
            cbNodes.getItems().addAll(sortedList);
        });
    }



    @FXML
    void setSearchFieldPromptText(String string) {
        this.searchField.setPromptText(string);
    }

}
