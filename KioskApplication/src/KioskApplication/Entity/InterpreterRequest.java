package KioskApplication.Entity;

import KioskApplication.database.objects.Node;

//Adds the field language
public class InterpreterRequest extends Request{
    String language;

    public InterpreterRequest(Node location, String employee, String language) {
        super(location, employee);
        this.language = language;
    }
}