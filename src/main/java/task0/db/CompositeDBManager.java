package main.java.task0.db;

import com.sun.istack.internal.Nullable;
import main.java.task0.*;
import main.java.task0.db.LevelDBManager.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

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

    // Testing code
    public static void importFromMysql() throws SQLException, LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();
        dbman.clearAll();

        ArrayList<Registration> mysqlRegistrations = DBManager.getInstance().findRegistrations();
        for(Registration r : mysqlRegistrations)
            dbman.addRegistration(r);
        dbman.dumpAll();

        // assert
        if(!CompositeDBManager.getInstance().isConsistent())
            throw new IllegalStateException("LevelDB registration list does not correspond to MySQL one");
    }

    public static boolean listDeepEqual(ArrayList<Registration> list1, ArrayList<Registration> list2) {
        //System.out.println("Deepequal debug:");
        if(list1.size() != list2.size())
            return false;

        HashSet<Integer> pickedFormList2 = new HashSet<>();

        // THIS ALGORITHM is O(n^2) and could be optimized with hashtables.
        // However we didn't implement any hashcode method
        for(int i=0; i<list1.size(); i++) {
            //System.out.println("Element " + i +":");
            //printRegistration.accept(list1.get(i));
            //System.out.println("v");
            boolean found = false;
            for(int j=0; j<list2.size(); j++) {
                //printRegistration.accept(list2.get(j));
                if (!pickedFormList2.contains(j) && list1.get(i).equals(list2.get(j))) {
                    found = true;
                    pickedFormList2.add(j);
                    break;
                }
            }

            if(!found)
                return false;
        }

        return true;
    }

    public boolean isConsistent() throws SQLException, LevelDBUnavailableException {
        return listDeepEqual(DBManager.getInstance().findRegistrations(), LevelDBManager.getInstance().findRegistrations());
    }

    public static void main(String[] args) throws SQLException, LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();

        System.out.println("-> importFromMysql started");
        importFromMysql();
        System.out.println("-> importFromMysql finished successfully");

        dbman.close();
    }
}
