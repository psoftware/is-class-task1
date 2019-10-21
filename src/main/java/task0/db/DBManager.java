/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0.db;

import com.sun.istack.internal.Nullable;
import main.java.task0.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adria
 */
public class DBManager {
    private EntityManagerFactory factory;
    private EntityManager entityManager;

    public void setup() {
        factory = Persistence.createEntityManagerFactory("Task0");

    }

    public void exit() {
        factory.close();
    }

    public static void main(String[] args) {
        DBManager manager = new DBManager();
        manager.setup();

        System.out.println("readRegistrationForStudent(1)");

        for(Registration r : manager.readRegistrationForStudent(1))
            System.out.println(r.getExam().getId().getDate().toString() + " " + r.getExam().getCourse().getName() + ": " + r.getGrade());

        //for(Course c : manager.findCourse(1))
        //    System.out.println("Course ("+c.getCfu()+")" + c.getName() + " of Professor " + c.getProfessor().getName() + " " + c.getProfessor().getSurname());

        manager.exit();
        System.out.println("Finished");
    }

    public List<Registration> readRegistrationForStudent(int studentId) {
        List<Registration> resultList;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT r FROM Registration r WHERE r.student.id = :studentId");
            query.setParameter("studentId", studentId);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            entityManager.close();
        }

        return resultList;
    }

    public List<Course> findCourse(int profID) {
        List<Course> resultList;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT c FROM Course c WHERE c.professor.id = :profID");
            query.setParameter("profID", profID);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            entityManager.close();
        }

        return resultList;
    }

    public void insertRegistration(Exam exam, Student student) {
        System.out.println("Creating a new Book");

        Registration registration = new Registration();
        registration.setExam(exam);
        registration.setStudent(student);

        try {
            entityManager = factory.createEntityManager();
            entityManager.getTransaction().begin();
            // Insert
            entityManager.persist(exam);
            // Update
            // entityManager.merge(exam);
            // Delete
            // entityManager.remove(exam);
            entityManager.getTransaction().commit();
            System.out.println("Registration Added");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("A problem occurred inserting a registration!");
        } finally {
            entityManager.close();
        }
    }

    /*
    public void insertExam (int courseID, LocalDate date) throws SQLException {
        try {
            String sql = "INSERT INTO exam (course, date) VALUES(?, ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseID);
            pstmt.setDate(2, Date.valueOf(date)); //imposta la data a un giorno precedente
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            TriggerSQLException.handleSqlException(ex);
        }
    }
    
    public ArrayList<Registration> findRegistrationProfessor (int id) throws SQLException {
        ArrayList<Registration> result = null;
        try {
            String sql = "SELECT c.*, e.*, s.*, pr.* FROM exam_result e " +
                    "INNER JOIN course c ON c.id = e.course " +
                    "INNER JOIN student s ON s.id = e.student " +
                    "INNER JOIN exam ex ON ex.course = c.id " +
                    "INNER JOIN professor pr ON pr.id = c.professor " +
                    "WHERE c.professor = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<Registration>();
            while (rs.next()) {
                Student student = new Student(rs.getInt("s.id"), rs.getString("s.name"), rs.getString("s.surname"));
                Professor professor = new Professor(rs.getInt("pr.id"), rs.getString("pr.name"), rs.getString("pr.surname"));
                Course course = new Course(rs.getInt("c.id"), rs.getString("c.name"), rs.getInt("c.cfu"), professor);
                Exam exam = new Exam(course, rs.getDate("date"));
                Registration reg = new Registration(student, exam, rs.getInt("grade"));
                result.add(reg);
            }
        } catch (SQLException ex) {
            TriggerSQLException.handleSqlException(ex);
        }
        return result;
    }
    
    public ArrayList<Registration> findRegistrationStudent (int id, boolean toDo) throws SQLException {
        ArrayList<Registration> result = null;
        try {
            String sql = "SELECT c.*, e.*, s.*, pr.* FROM exam_result e " +
                    "INNER JOIN course c ON c.id = e.course " +
                    "INNER JOIN student s ON s.id = e.student " +
                    "INNER JOIN professor pr ON pr.id = c.professor " +
                    "INNER JOIN exam ex ON ex.course = c.id ";
            if (toDo)
                sql += "WHERE e.student = ? AND grade is NULL";
            else
                sql += "WHERE e.student = ? AND grade is not NULL";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<Registration>();
            while (rs.next()) {
                Student student = new Student(rs.getInt("s.id"), rs.getString("s.name"), rs.getString("s.surname"));
                Professor professor = new Professor(rs.getInt("pr.id"), rs.getString("pr.name"), rs.getString("pr.surname"));
                Course course = new Course(rs.getInt("c.id"), rs.getString("c.name"), rs.getInt("c.cfu"), professor);
                Exam exam = new Exam(course, rs.getDate("date"));
                Registration reg = new Registration(student, exam, rs.getInt("grade"));
                result.add(reg);
            }
        } catch (SQLException ex) {
            TriggerSQLException.handleSqlException(ex);
        }
        return result;
    }

    public void updateRegistration (Registration reg, int grade) throws SQLException {
        try {
            String sql = "UPDATE exam_result SET grade = ? WHERE (student = ?) and (course = ?) and (date = ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, grade);
            pstmt.setInt(2, reg.getStudent().getId());
            pstmt.setInt(3, reg.getExam().getCourse().getId());
            pstmt.setDate(4, reg.getExam().getDate());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            TriggerSQLException.handleSqlException(ex);
        }
    }

    public void deleteRegistration (int studentId, Exam exam) throws SQLException {
        try {
            String sql = "DELETE FROM exam_result WHERE (student = ?) and (course = ?) and (date = ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, exam.getCourse().getId());
            pstmt.setDate(3, exam.getDate());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            TriggerSQLException.handleSqlException(ex);
        }
    }
    
    public ArrayList<Exam> findExam () throws SQLException {
        ArrayList<Exam> result = null;
        try {
            String sql = "SELECT c.*, e.date, pr.* FROM exam e " +
                    "INNER JOIN course c ON c.id = e.course " +
                    "INNER JOIN professor pr ON pr.id = c.professor; ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<>();
            while (rs.next()) {
                Professor professor = new Professor(rs.getInt("pr.id"), rs.getString("pr.name"), rs.getString("pr.surname"));
                Course c = new Course(rs.getInt("c.id"), rs.getString("c.name"), rs.getInt("c.cfu"), professor);
                Exam e = new Exam(c, rs.getDate("e.date"));
                result.add(e);
            }
        } catch (SQLException ex) {
            TriggerSQLException.handleSqlException(ex);
        }
        return result;
    }

    public void insertRegistration (int studentId, Exam exam, @Nullable Integer grade) throws SQLException {
        try {
            String sql = "INSERT INTO exam_result (student, course, date, grade) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, exam.getCourse().getId());
            pstmt.setDate(3, exam.getDate());
            if(grade == null)
                pstmt.setNull(4, Types.INTEGER);
            else
                pstmt.setInt(4, grade);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            TriggerSQLException.handleSqlException(ex);
        }
    }

    public static class TriggerSQLException extends SQLException {
        private String error;
        private TriggerSQLException(SQLException sqlEx) {
            super(new SQLException(sqlEx));
            this.error = sqlEx.getMessage();
        }

        public String getTriggerMessage() {
            return error;
        }

        public static void ifFromTrigger(SQLException ex) throws SQLException {
            if(ex.getSQLState().equals("02000")) {
                throw new TriggerSQLException(ex);
            } else
                throw ex;
        }

        protected static void handleSqlException(SQLException ex) throws SQLException {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            TriggerSQLException.ifFromTrigger(ex);
        }
    }
     */
}
