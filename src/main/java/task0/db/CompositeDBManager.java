package main.java.task0.db;

import org.jetbrains.annotations.Nullable;
import main.java.task0.*;
import main.java.task0.db.LevelDBManager.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class CompositeDBManager {
    private static CompositeDBManager INSTANCE;

    private DBManager mysqlDBMan;
    private LevelDBManager levelDBManager;

    public enum QueryExecutor{MySQL, LevelDB, Both};
    private QueryExecutor lastExecutor;

    public static CompositeDBManager getInstance() throws SQLException, LevelDBUnavailableException {
        if(INSTANCE == null) {
            INSTANCE = new CompositeDBManager();
            INSTANCE.importFromMysql();
        }
        return INSTANCE;
    }

    public CompositeDBManager() throws SQLException {
        mysqlDBMan = DBManager.getInstance();
        levelDBManager = LevelDBManager.getInstance();
    }

    public QueryExecutor getLastExecutor() {
        return lastExecutor;
    }

    public List<Course> findCourse(int profID) throws SQLException {
        lastExecutor = QueryExecutor.MySQL;
        return mysqlDBMan.findCourse(profID);
    }

    public void insertExam(int courseID, LocalDate date) throws SQLException {
        lastExecutor = QueryExecutor.MySQL;
        mysqlDBMan.insertExam(courseID, date);
    }

    public List<Registration> findRegistrations() throws SQLException {
        try {
            lastExecutor = QueryExecutor.LevelDB;
            return levelDBManager.findRegistrations();
        } catch (LevelDBUnavailableException e) {
            lastExecutor = QueryExecutor.MySQL;
            return mysqlDBMan.findRegistrations();
        }
    }

    public List<Registration> findRegistrationProfessor(int id) throws SQLException {
        try {
            lastExecutor = QueryExecutor.LevelDB;
            return levelDBManager.findRegistrationProfessor(id);
        } catch (LevelDBUnavailableException e) {
            lastExecutor = QueryExecutor.MySQL;
            return mysqlDBMan.findRegistrationProfessor(id);
        }
    }

    public List<Registration> findRegistrationStudent(int id, boolean toDo) throws SQLException {
        try {
            lastExecutor = QueryExecutor.LevelDB;
            return levelDBManager.findRegistrationStudent(id, toDo);
        } catch (LevelDBUnavailableException e) {
            lastExecutor = QueryExecutor.MySQL;
            return mysqlDBMan.findRegistrationStudent(id, toDo);
        }
    }

    public void updateRegistration(Registration reg, int grade)
            throws SQLException, LevelDBUnavailableException, InconsistentDatabaseException {
        try {
            mysqlDBMan.startTransaction();
            lastExecutor = QueryExecutor.Both;
            mysqlDBMan.updateRegistration(reg, grade);
            mysqlDBMan.flushTransaction();
            levelDBManager.updateRegistration(reg, grade);
            mysqlDBMan.commitTransaction();
        } catch (Exception e) {
            mysqlDBMan.rollbackTransaction();
            throw e;
        } finally {
            checkConsistency();
        }
    }

    public void deleteRegistration(int studentId, Exam exam)
            throws SQLException, LevelDBUnavailableException, InconsistentDatabaseException {
        try {
            mysqlDBMan.startTransaction();
            lastExecutor = QueryExecutor.Both;
            mysqlDBMan.deleteRegistration(studentId, exam);
            mysqlDBMan.flushTransaction();
            levelDBManager.deleteRegistration(studentId, exam);
            mysqlDBMan.commitTransaction();
        } catch (Exception e) {
            mysqlDBMan.rollbackTransaction();
            throw e;
        } finally {
            checkConsistency();
        }
    }

    public List<Exam> findExam(int studentId) throws SQLException {
        lastExecutor = QueryExecutor.MySQL;
        return mysqlDBMan.findExam(studentId);
    }

    public void insertRegistration(int studentId, Exam exam, @Nullable Integer grade)
            throws SQLException, LevelDBUnavailableException, InconsistentDatabaseException {
        try {
            mysqlDBMan.startTransaction();
            lastExecutor = QueryExecutor.Both;
            Student student = mysqlDBMan.findStudent(studentId);
            if(student == null)
                throw new IllegalStateException("No student result associated to Student ID");
            mysqlDBMan.insertRegistration(studentId, exam, grade);
            mysqlDBMan.flushTransaction();
            levelDBManager.insertRegistration(student, exam, grade);
            mysqlDBMan.commitTransaction();
        } catch (Exception e) {
            mysqlDBMan.rollbackTransaction();
            throw e;
        } finally {
            checkConsistency();
        }
    }

    public void close() {
        levelDBManager.close();
    }

    // Testing code
    public static void importFromMysql() throws SQLException, LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();
        dbman.clearAll();

        List<Registration> mysqlRegistrations = DBManager.getInstance().findRegistrations();
        for(Registration r : mysqlRegistrations)
            dbman.addRegistration(r);
        dbman.dumpAll();

        // assert
        if(!CompositeDBManager.getInstance().isConsistent())
            throw new IllegalStateException("LevelDB registration list does not correspond to MySQL one");
    }

    public static boolean listDeepEqual(List<Registration> list1, List<Registration> list2) {
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

    public void checkConsistency() throws SQLException, InconsistentDatabaseException {
        try {
            if(!isConsistent())
                throw new InconsistentDatabaseException("LevelDB/MySQL database is inconsistent! This should not happen");
        } catch (LevelDBUnavailableException e) {
            System.out.println("Cannot check consistency: LevelDB is unavailable");
        }
    }

    public static void main(String[] args) throws SQLException, LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();

        System.out.println("-> importFromMysql started");
        importFromMysql();
        System.out.println("-> importFromMysql finished successfully");

        dbman.close();
    }

    public static class InconsistentDatabaseException extends Exception {
        public InconsistentDatabaseException(String msg) {
            super(msg);
        }
    }
}
