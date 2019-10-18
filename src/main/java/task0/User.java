package main.java.task0;

import main.java.task0.db.DBManager;
import main.java.task0.gui.Task0GUI;

import java.sql.Date;
import java.util.List;

public class User {
    private static User user;

    private DBManager dbManager;
    private User() {
        dbManager = new DBManager("127.0.0.1", 3306, "Task0", "root", "root");
        dbManager.connect();
    }

    public static User getInstance() {
        if(user == null)
            user = new User();
        return user;
    }

    public static DBManager getDbManager() {
        return getInstance().dbManager;
    }

    public void addExam(int courseId, Date date) {
        System.out.println(date);
        dbManager.insertExam(courseId, date);
    }

    public void listExams(Task0GUI gui) {
        List list = dbManager.findExam();
        gui.setTableExams(list);
    }

    public void listCourses(Task0GUI gui, int professorId) {
        List list = dbManager.findCourse(professorId);
        gui.setTableCourses(list);
    }
}
