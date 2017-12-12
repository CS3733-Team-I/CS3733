package utility.validators;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;


@DefaultProperty(value = "icon")
public class MatchingFieldValidator extends ValidatorBase {
    private String comparison;

    @Override
    protected void eval() {
        if(srcControl.get() instanceof TextInputControl){
            evalCompareTextInputFields();
        }
    }

    private void evalCompareTextInputFields() {
        TextInputControl textField = ((TextInputControl) srcControl.get());
        if(textField.getText().equals(comparison)){
            hasErrors.set(false);
        }
        else {
            hasErrors.set(true);
        }
    }

    /**
     * Used to set which field the validator compares to
     * @param comparison
     */
    public void compareTo(String comparison){
        this.comparison=comparison;
    }
}
