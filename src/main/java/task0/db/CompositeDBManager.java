package main.java.task0.db;

import com.sun.istack.internal.Nullable;
import main.java.task0.Course;
import main.java.task0.Exam;
import main.java.task0.Registration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CompositeDBManager {
    private static CompositeDBManager INSTANCE;

    private DBManager mysqlDBMan;
    private LevelDBManager levelDBManager;

    public static CompositeDBManager getInstance() throws SQLException {
        if(INSTANCE == null)
            INSTANCE = new CompositeDBManager();
        return INSTANCE;
    }

    public CompositeDBManager() throws SQLException {
        mysqlDBMan = DBManager.getInstance();
        levelDBManager = LevelDBManager.getInstance();
    }

    public ArrayList<Course> findCourse(int profID) throws SQLException {
        return mysqlDBMan.findCourse(profID);
    }

    public void insertExam(int courseID, LocalDate date) throws SQLException {
        mysqlDBMan.insertExam(courseID, date);
    }

    public ArrayList<Registration> findRegistrations() throws SQLException {
        return mysqlDBMan.findRegistrations();
    }

    public ArrayList<Registration> findRegistrationProfessor(int id) throws SQLException {
        return mysqlDBMan.findRegistrationProfessor(id);
    }

    public ArrayList<Registration> findRegistrationStudent(int id, boolean toDo) throws SQLException {
        return mysqlDBMan.findRegistrationStudent(id, toDo);
    }

    public void updateRegistration(Registration reg, int grade) throws SQLException {
        mysqlDBMan.updateRegistration(reg, grade);
    }

    public void deleteRegistration(int studentId, Exam exam) throws SQLException {
        mysqlDBMan.deleteRegistration(studentId, exam);
    }

    public ArrayList<Exam> findExam(int studentId) throws SQLException {
        return mysqlDBMan.findExam(studentId);
    }

    public void insertRegistration(int studentId, Exam exam, @Nullable Integer grade) throws SQLException {
        mysqlDBMan.insertRegistration(studentId, exam, grade);
    }

    public void close() {
        try {
            mysqlDBMan.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        levelDBManager.close();
    }
}
