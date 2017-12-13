package FoodRequestAPI.utility.request;

import java.util.Comparator;

public class SortByFrequency implements Comparator<LanguageFrequency> {
    public int compare(LanguageFrequency a, LanguageFrequency b){
        return b.frequency-a.frequency;
    }
}
