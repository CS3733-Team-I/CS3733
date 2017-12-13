package FoodRequestAPI.utility.request;

//pairing for languages and their frequency
public class LanguageFrequency {
    Language language;
    int frequency;

    public LanguageFrequency(Language language, int frequency){
        this.language=language;
        this.frequency=frequency;
    }

    public void increment(){
        this.frequency=frequency+1;
    }

    public boolean isLanguage(Language language){
        return this.language ==language;
    }

    public Language getLanguage() {
        return language;
    }

    public int getFrequency() {
        return frequency;
    }
}
