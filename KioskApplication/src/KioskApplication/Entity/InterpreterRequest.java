package KioskApplication.Entity;

import KioskApplication.database.objects.Node;

public class InterpreterRequest extends Request{
    String language;

    public InterpreterRequest(Node location, String employee, String language) {
        super(location, employee);
        this.language = language;
    }
}
