package utility.Request;

import javafx.scene.chart.XYChart;

//pairing for languages and their frequency
public class LanguageFrequency {
    Language language;
    int frequency;

    public LanguageFrequency(Language language){
        this.language=language;
        this.frequency=0;
    }

    public void increment(){
        this.frequency=frequency+1;
    }

    public boolean isLanguage(Language language){
        return this.language ==language;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if(obj.getClass() == this.getClass()){
            LanguageFrequency other = (LanguageFrequency)obj;
            return other.language==this.language;
        }
        else {
            return false;
        }
    }

    public Language getLanguage() {
        return language;
    }

    public int getFrequency() {
        return frequency;
    }
}
