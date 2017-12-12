package controller;

import com.jfoenix.controls.JFXComboBox;
import entity.SearchEntity.ISearchEntity;
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

import java.util.*;
import java.util.function.Predicate;

public class SearchController {

    @FXML
    private JFXComboBox<ISearchEntity> cbSearchData;

    private FilteredList<ISearchEntity> filteredList;

    private ObservableList<ISearchEntity> searchData;

    private SortedList<ISearchEntity> sortedList;

    private ScreenController parent;

    private TableView<ISearchEntity> searchTable;

    private HashMap<String, ISearchEntity> searchMap;

    public SearchController(ScreenController parent, ArrayList<ISearchEntity> searchData) {
        this.parent = parent;
        this.searchData = FXCollections.observableArrayList();
        this.searchData.addAll(searchData);
        searchMap = new HashMap<>();
        for(ISearchEntity searchEntity : searchData) {
            searchMap.put(searchEntity.getComparingString(), searchEntity);
        }
    }

    @FXML
    void initialize() {

        //initialize the lists
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
                        searchResults.addAll(fuzzySearch(newValue));
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
        this.searchData.clear();
        this.searchData.addAll(searchData);
        for(ISearchEntity searchEntity : searchData) {
            searchMap.put(searchEntity.getComparingString(), searchEntity);
        }
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

    /**
     * Fuzzy search
     * goes through all ISearchEntity long names and returns the top 5 matches
     *
     * @param inputtext
     * @return Linked list of 5 top search result nodes
     */
     private LinkedList<ISearchEntity> fuzzySearch(String inputtext) throws Exception {// add boolean
        if (!inputtext.isEmpty()) {
            HashMap<String,ISearchEntity> allsearch = new HashMap<>();
            allsearch.putAll(searchMap);
            inputtext = inputtext.replaceAll("\\s+","");
            String[] input = inputtext.split("");
            Map<String, Integer> sortedMap = new HashMap<>();
            int matched = 0;
            // put all nodes in hash map
            // go through all nodes and get hightest match in a hashmap with key of node
            for (String key : allsearch.keySet()) {
                matched = 0;
                String longname = allsearch.get(key).getName().replaceAll("\\s+","");
                String[] longName = longname.split("");
                for(int i = 0; i<input.length; i++){
                    if(longName.length>i) {
                        if (longName[i].toLowerCase().equals(input[i].toLowerCase())) {
                            matched=matched+20; }
                    }
                    else{
                        if (input[i].toLowerCase().equals(longName[longName.length - 1].toLowerCase())) {
                            matched++;
                        }
                    }
                }
                sortedMap.put(key, matched);
            }
            // now sort hashmap from highest to lowest using array list sort and compare
            ArrayList<Map.Entry<String, Integer>> sorted = new ArrayList<>(sortedMap.entrySet());
            Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    }
            );
          /*ArrayList<Map.Entry<String, Integer>> sorted = new ArrayList<>();
           // List<String> sorted = new ArrayList<>();
            for (String key : sortedMap.keySet()) {
               sorted.add(sortedMap.get(key),key);
            }*/
            LinkedList<ISearchEntity> bestmatch = new LinkedList<>();
            if(sorted.size()>5) {
                // get the top 5 from sorted arraylist
                for (int i = 0; i < 5; i++) {
                    bestmatch.add(allsearch.get(sorted.get(i).getKey()));
                }
                return bestmatch;
            }
            else{
                for(int i=0; i<sorted.size();i++){
                    bestmatch.add(allsearch.get(sorted.get(i).getKey()));
                }
                return  bestmatch;
            }
        } else {
            // input is 0 return exceptipon
            throw new Exception("No Input Text");
        }
    }
}
