package controller;

import com.jfoenix.controls.JFXComboBox;
import entity.SearchEntity.ISearchEntity;
import entity.SearchEntity.SearchNode;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private Object parent;

    private HashMap<String, ISearchEntity> searchMap;

    public SearchController(Object parent, ArrayList<ISearchEntity> searchData) {
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
        filteredList = new FilteredList<>(this.searchData, event -> true);
        //set the combo box style and editable
        cbSearchData.setEditable(true);
        cbSearchData.setPromptText("Search by location, doctor or type");
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

        //DONT REMOVE THIS
        cbSearchData.setCellFactory(listView -> new ListCell<ISearchEntity>() {
            @Override
            protected void updateItem(ISearchEntity item, boolean empty) {
                super.updateItem(item, empty);

                getListView().setMaxWidth(750);
                if(empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item.getIcon());
                    setText(item.getSearchString());
                }
            }
        });

        cbSearchData.getEditor().focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                cbSearchData.show();
                cbSearchData.setPromptText("");
            } else {
                cbSearchData.hide();
                cbSearchData.setPromptText("Search by location, doctor or type"); // TODO use language string
            }
        }));

        cbSearchData.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> updateSearch(newValue));

        updateSearch("");
        cbSearchData.hide();
    }

    private void updateSearch(String searchText) {
        final List<ISearchEntity> searchResults = new LinkedList<>();
        if (searchText != null && !searchText.equals("")) {
            try {
                searchResults.addAll(fuzzySearch(searchText));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        filteredList.setPredicate((Predicate<? super ISearchEntity>) entity -> {
            if(searchText == null || searchText.isEmpty()) {
                return true;
            }

            for (ISearchEntity searchEntity : searchResults) {
                if (searchEntity.getComparingString().equalsIgnoreCase(entity.getComparingString())) {
                    return true;
                }
            }

            return false;
        });

        cbSearchData.getItems().clear();
        cbSearchData.getItems().addAll(searchResults);
        if(!searchResults.isEmpty()) {
            cbSearchData.show();
        } else {
            cbSearchData.hide();
        }
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
    }

    public void setVisible(boolean b) {
        this.cbSearchData.setVisible(b);
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
                    bestmatch.addLast(allsearch.get(sorted.get(i).getKey()));
                }
                return bestmatch;
            }
            else{
                for(int i=0; i<sorted.size();i++){
                    bestmatch.addLast(allsearch.get(sorted.get(i).getKey()));
                }
                return  bestmatch;
            }
        } else {
            // input is 0 return exceptipon
            throw new Exception("No Input Text");
        }
    }

    public void addSearchData(ISearchEntity iSearchEntity) {
        this.searchData.add(iSearchEntity);
        this.searchMap.put(iSearchEntity.getComparingString(), iSearchEntity);
    }

    public void changeSearchData(String comparingString, ISearchEntity iSearchEntity) {
        ListIterator<ISearchEntity> iterator = searchData.listIterator();
        while (iterator.hasNext()) {
            ISearchEntity next = iterator.next();
            if (next.getComparingString().equals(comparingString)) {
                //Replace element
                iterator.set(iSearchEntity);
            }
        }
        //update hashmap
         if(searchMap.containsKey(comparingString)) {
             this.searchMap.put(comparingString, iSearchEntity);
         }
         else {
             Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setTitle("Can't Match Search Data while updating search data" + comparingString);
             alert.showAndWait();
         }
    }

    public void removeSearchData(String comparingString) {
        ListIterator<ISearchEntity> iterator = searchData.listIterator();
        while (iterator.hasNext()) {
            ISearchEntity next = iterator.next();
            if (next.getComparingString().equals(comparingString)) {
                //Replace element
                iterator.remove();
            }
        }
        //update hashmap
        if(searchMap.containsKey(comparingString)) {
            this.searchMap.remove(comparingString);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Can't Match Search Data while removing search data" + comparingString);
            alert.showAndWait();
        }
    }
}
