package entity.SearchEntity;

import javafx.scene.image.ImageView;

public interface ISearchEntity {
    /**
     * get the search data in the class that implements this interface
     */
    public Object getData();
    /**
     * get the string that will be displaying in the search bar
     */
    public String getSearchString();
    /**
     * get the icon that will be displaying in the search bar
     */
    public ImageView getIcon();
    /**
     * get the string to search by
     * SearchNode = NodeID
     * SearchRequest = RequestID
     */
    public String getComparingString();
    /**
     * get search data's name
     * SearchNode = longName
     * SearchRequest = getSearchString()
     */
    public String getName();
}
