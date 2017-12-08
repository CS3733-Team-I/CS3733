package controller;

import com.jfoenix.controls.JFXTextField;
import database.objects.Node;
import entity.MapEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;

import java.awt.*;

public class SearchController {

    @FXML
    private JFXTextField searchField;

    private FilteredList<Node> filteredList;

    private ObservableList<Node> nodeData;

    private ScreenController parent;

    public SearchController(ScreenController parent) {
        this.parent = parent;
        nodeData = FXCollections.observableArrayList(MapEntity.getInstance().getAllNodes());
    }

    @FXML
    void initialize() {
        filteredList = new FilteredList<Node>(nodeData, e->true);
//        searchField.
    }

    @FXML
    void setSearchFieldPromptText(String string) {
        this.searchField.setPromptText(string);
    }

}
