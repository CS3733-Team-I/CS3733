package entity;

import database.DatabaseController;
import database.objects.ActivityLog.ActivityLog;
import database.objects.ActivityLog.ActivityType;
import database.objects.Employee;
import database.utility.DatabaseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utility.KioskPermission;
import utility.request.RequestType;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestActivityLogger {
    private DatabaseController databaseController;
    private ActivityLogger activityLogger;
    private LoginEntity loginEntity;
    private Employee employee1;
    private Employee employee2;

    @Before
    public void setup(){
        databaseController=DatabaseController.getInstance();
        activityLogger=ActivityLogger.getInstance();
        loginEntity=LoginEntity.getInstance();
        employee1 = new Employee("ww123","Wong","Wilson","password",
                new ArrayList<>(), KioskPermission.SUPER_USER, RequestType.GENERAL);
        employee2 = new Employee(0,"JT","Taylor","James","password",
                ":", KioskPermission.EMPLOYEE, RequestType.SECURITY);
    }

    @After
    public void cleanUp(){
        for (ActivityLog log: activityLogger.getAllLogs()){
            databaseController.removeActivityLog(log);
        }
        try{
            for (Employee employee: databaseController.getAllEmployees()){
                databaseController.removeEmployee(employee.getID());
            }
        } catch (DatabaseException e){

        }
    }

    @Test
    public void testLogEmployeeAdd() throws DatabaseException{
        int emp1ID = databaseController.addEmployee(employee1, "password");
        employee1.setId(emp1ID);
        loginEntity.readAllFromDatabase();
        loginEntity.logIn(employee1.getUsername(),"password");
        activityLogger.logEmployeeAdd(employee1,employee2);
        ActivityLog log = activityLogger.getAllLogs().getLast();
        assertEquals(employee1.getID(),log.getEmployeeID());
        assertEquals(ActivityType.EMPLOYEE,log.getActivityType());
    }
}
