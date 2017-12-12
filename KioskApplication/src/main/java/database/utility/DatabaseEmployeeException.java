package database.utility;

import database.objects.Employee;

public class DatabaseEmployeeException extends DatabaseException {
    Employee employee;

    public DatabaseEmployeeException(Employee employee, DatabaseExceptionType type){
        super(type);
        this.employee=employee;
    }

    @Override
    public void printStackTrace() {
        System.err.println(toString());
        super.printStackTrace();
    }

    @Override
    public String toString() {
        return ("Database Exception of type " + type.name() + " for employee: " + employee.getUsername());
    }
}
