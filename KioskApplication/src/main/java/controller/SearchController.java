package controller;

import com.jfoenix.controls.JFXComboBox;
import database.objects.Node;
import entity.searchEntity.ISearchEntity;
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
import pathfinder.NodeFuzzySearch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class SearchController {

    @FXML
    private JFXComboBox<ISearchEntity> cbSearchData;

    private FilteredList<ISearchEntity> filteredList;

    private ObservableList<ISearchEntity> searchData;

    private SortedList<ISearchEntity> sortedList;

    private ScreenController parent;

    private TableView<ISearchEntity> searchTable;
    NodeFuzzySearch nodeFuzzySearch;
    String value;

    public SearchController(ScreenController parent, ArrayList<ISearchEntity> searchData) {
        this.parent = parent;
        this.searchData = FXCollections.observableArrayList();
        this.searchData.addAll(searchData);
//        for(Node databaseNode :MapEntity.getInstance().getAllNodes()) {
//            if(databaseNode.getNodeType() != NodeType.HALL) {
//                searchData.add(new SearchNode(databaseNode));
//            }
//        }
    }

    @FXML
    void initialize() {

        //initialize the lists
        nodeFuzzySearch = new NodeFuzzySearch();
        searchTable = new TableView<>();
        this.filteredList = new FilteredList<>(this.searchData, e->true);
        //set the combo box style and editable
        cbSearchData.setTooltip(new Tooltip());
        cbSearchData.setEditable(true);
        cbSearchData.setPromptText("Search by location, doctor or type");
        cbSearchData.getEditor().setEditable(true);
        cbSearchData.setConverter(new StringConverter<ISearchEntity>() {
            @Override
            public String toString(ISearchEntity object) {
                if(object != null) {
                    return object.getSearchString();
                }
                else return null;
            }

            @Override
            public ISearchEntity fromString(String string) {
                return cbSearchData.getItems().stream().filter(ISearchEntity ->
                        ISearchEntity.getSearchString().equals(string)).findFirst().orElse(null);
            }
        });

        cbSearchData.setCellFactory(listView -> new ListCell<ISearchEntity>() {
            @Override
            protected void updateItem(ISearchEntity item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty) {
                    setGraphic(null);
                }
                else {
                    setGraphic(item.getIcon());
                    setText(item.getSearchString());
                }
            }
        });

        cbSearchData.setButtonCell(cbSearchData.getCellFactory().call(null));

        cbSearchData.setOnKeyReleased(e -> {
            cbSearchData.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
                final List<ISearchEntity> searchResults = new LinkedList<>();
                if (newValue != null && !newValue.equals("")) {
                    try {
                        searchResults.addAll(NodeFuzzySearch.fuzzySearch(newValue,));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                filteredList.setPredicate((Predicate<? super ISearchEntity>) iSearchEntity-> {
                    if(newValue == null || newValue.isEmpty()) {
                        return true;
                    } else {
                        cbSearchData.setPromptText("");
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

                    for (ISearchEntity searchEntity : searchResults) {
                        //TODO Search String vs Node ID?
                        if (searchEntity.getComparingString().equalsIgnoreCase(iSearchEntity.getComparingString())) {
                            return true;
                        }
                    }

                    return false;
                });
            });
            sortedList = new SortedList<>(filteredList);

            sortedList.comparatorProperty().bind(searchTable.comparatorProperty());
            searchTable.setItems(sortedList);
            cbSearchData.getItems().clear();
            cbSearchData.getItems().addAll(sortedList);
            if(!sortedList.isEmpty()) {
                cbSearchData.show();
            }
            else {
                cbSearchData.hide();
            }
        });
    }

    @FXML
    void setSearchFieldPromptText(String string) {
        this.cbSearchData.getEditor().setPromptText(string);
        this.cbSearchData.setPromptText(string);}

    public Object getSelected() {
        return cbSearchData.getSelectionModel().getSelectedItem().getData();
    }

    public boolean isSelected() {
        return cbSearchData.getSelectionModel().isEmpty();
    }

    public void reset(ArrayList<ISearchEntity> searchData) {
        searchData.clear();
        searchData.addAll(searchData);
    }

    public ObjectProperty<ISearchEntity> getCBValueProperty() {
        return cbSearchData.valueProperty();
    }

    public void resizeSearchbarWidth(double width) {
        this.cbSearchData.setPrefWidth(width);
    }

    public void clearSearch() {
        cbSearchData.getItems().clear();
        sortedList.clear();
        searchTable.getItems().clear();
    }
}
