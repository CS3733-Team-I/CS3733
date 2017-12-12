package entity.SearchEntity;

import database.objects.Node;
import javafx.scene.image.ImageView;

public interface ISearchEntity {
    /**
     * get the search data in the class that implements this interface
     */
    Object getData();
    /**
     * get the string that will be displaying in the search bar
     */
    String getSearchString();
    /**
     * get the icon that will be displaying in the search bar
     */
    ImageView getIcon();
    /**
     * get the string to search by
     * SearchNode = NodeID
     * SearchRequest = RequestID
     */
    String getComparingString();
    /**
     * get search data's name
     * SearchNode = longName
     * SearchRequest = getSearchString()
     */
    String getName();

    Node getLocation();
}
