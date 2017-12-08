package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.objects.Node;
import entity.MapEntity;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.function.Predicate;

public class SearchController {

    @FXML
    private JFXComboBox<Node> cbNodes;

    private FilteredList<Node> filteredList;

    private ObservableList<Node> nodeData;

    private SortedList<Node> sortedList;

    private ScreenController parent;

    private TableView<Node> nodeTable;

    public SearchController(ScreenController parent) {
        this.parent = parent;
        nodeData = FXCollections.observableArrayList(MapEntity.getInstance().getAllNodes());
    }

    @FXML
    void initialize() {

        //initialize the lists
        nodeTable = new TableView<>();
        filteredList = new FilteredList<Node>(nodeData, e->true);
        //set the combo box style and editable
        cbNodes.setTooltip(new Tooltip());
        cbNodes.setEditable(true);
        cbNodes.setPromptText("Search");
        cbNodes.getEditor().setEditable(true);
        cbNodes.setConverter(new StringConverter<Node>() {
            @Override
            public String toString(Node object) {
                if(object != null) {
                    return object.getLongName() + " (" + object.getFloor() + ") ";
                }
                else return null;
            }

            @Override
            public Node fromString(String string) {
                return cbNodes.getItems().stream().filter(node ->
                node.getLongName().equals(string)).findFirst().orElse(null);
            }
        });
//        cbNodes.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
//            @Override
//            public ListCell<Node> call(ListView<Node> param) {
//
//            }
//        });
        cbNodes.setOnKeyReleased(e -> {
            cbNodes.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Node>) node-> {
                    if(newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    //also check its lower case
                    String lowerCaseFilter = newValue.toLowerCase();
                    //also check its upper case
                    String upperCaseFilter = newValue.toUpperCase();
                    if(node.getNodeID().contains(newValue) || node.getLongName().contains(newValue) || node.getShortName().contains(newValue)) {
                        return true;
                    }else if(node.getNodeID().contains(lowerCaseFilter) || node.getLongName().contains(lowerCaseFilter) || node.getShortName().contains(lowerCaseFilter)) {
                        return true;
                    }
                    else if(node.getNodeID().contains(upperCaseFilter) || node.getLongName().contains(upperCaseFilter) || node.getShortName().contains(upperCaseFilter)) {
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
        return cbNodes.getSelectionModel().getSelectedItem();
    }

    public boolean isSelected() {
        return cbNodes.getSelectionModel().isEmpty();
    }


    public ObjectProperty<Node> getCBValueProperty() {
        return cbNodes.valueProperty();
    }

    public void clearSearch() {
        cbNodes.getItems().clear();
        sortedList.clear();
        nodeTable.getItems().clear();
    }
}
