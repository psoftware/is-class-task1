/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0.db;

import com.sun.istack.internal.Nullable;
import main.java.task0.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author adria
 */
public class DBManager {
    private static DBManager INSTANCE;

    private String address;
    private int port;
    private String DBName;
    private String user;
    private String password;
    
    private Connection conn;
    
    public DBManager (String address, int port, String DBName, String user, String password) {
        this.DBName = DBName;
        this.port = port;
        this.password = password;
        this.user = user;
        this.address = address;
        
        connect();
    }

    public static DBManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DBManager("127.0.0.1", 3306, "Task0", "root", "root");
            INSTANCE.connect();
        }
        return INSTANCE;
    }
    
    public void connect () {
        try {
            String url = "jdbc:mysql://" + address + ":" + Integer.toString(port) + "/" + DBName + "?&serverTimezone=UTC";
            System.out.println(url);
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
        }        
    }
    
    public void disconnect () {
        try {
            conn.close();
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
    
    public ArrayList<Course> findCourse (int profID) {
        ArrayList<Course> result = null;
        try {
            String sql = "SELECT c.*, pr.* FROM course c INNER JOIN professor pr ON pr.id = professor WHERE professor = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, profID);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<Course>();
            while (rs.next()){
                Professor professor = new Professor(rs.getInt("pr.id"), rs.getString("pr.name"), rs.getString("pr.surname"));
                Course c = new Course(rs.getInt("id"), rs.getString("name"), rs.getInt("cfu"), professor);
                result.add(c);
            }
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());           
        }
        return result;
    }
    
    public void insertExam (int courseID, LocalDate date) throws TriggerSQLException {
        try {
            String sql = "INSERT INTO exam (course, date) VALUES(?, ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseID);
            pstmt.setDate(2, Date.valueOf(date)); //imposta la data a un giorno precedente
            pstmt.executeUpdate();
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
           TriggerSQLException.ifFromTrigger(ex);
        }
    }
    
    public ArrayList<Registration> findRegistrationProfessor (int id) {
        ArrayList<Registration> result = null;
        try {
            String sql = "SELECT c.id, c.name, c.cfu, c.professor, student, e.date, e.grade, s.name, s.surname, pr.id, pr.name, pr.surname FROM exam_result e " +
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
                Student student = new Student(rs.getInt("student"), rs.getString("s.name"), rs.getString("s.surname"));
                Professor professor = new Professor(rs.getInt("pr.id"), rs.getString("pr.name"), rs.getString("pr.surname"));
                Course course = new Course(rs.getInt("c.id"), rs.getString("c.name"), rs.getInt("c.cfu"), professor);
                Exam exam = new Exam(course, rs.getDate("date"));
                Registration reg = new Registration(student, exam, rs.getInt("grade"));
                result.add(reg);
            }
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());            
        }
        return result;
    }
    
    public ArrayList<Registration> findRegistrationStudent (int id, boolean toDo) {
        ArrayList<Registration> result = null;
        try {
            String sql = "SELECT c.id, c.name, c.cfu, c.professor, student, e.date, e.grade, " +
                    "s.name, s.surname, pr.id, pr.name, pr.surname FROM exam_result e " +
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
                Student student = new Student(rs.getInt("student"), rs.getString("s.name"), rs.getString("s.surname"));
                Professor professor = new Professor(rs.getInt("pr.id"), rs.getString("pr.name"), rs.getString("pr.surname"));
                Course course = new Course(rs.getInt("c.id"), rs.getString("c.name"), rs.getInt("c.cfu"), professor);
                Exam exam = new Exam(course, rs.getDate("date"));
                Registration reg = new Registration(student, exam, rs.getInt("grade"));
                result.add(reg);
            }
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());            
        }
        return result;
    }
    
    public void updateRegistration (int student, Date date, int course, int grade) throws TriggerSQLException {
        try {
            String sql = "UPDATE exam_result SET grade = ? WHERE (student = ?) and (course = ?) and (date = ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, grade);
            pstmt.setInt(2, student);
            pstmt.setInt(3, course);
            pstmt.setDate(4, date);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
           TriggerSQLException.ifFromTrigger(ex);
        }
    }
    
    public void deleteRegistration (int student, int course, Date date) {
        try {
            String sql = "DELETE FROM exam_result WHERE (student = ?) and (course = ?) and (date = ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, student);
            pstmt.setInt(2, course);
            pstmt.setDate(3, date);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
    
    public ArrayList<Exam> findExam () {
        ArrayList<Exam> result = null;
        try {
            String sql = "SELECT c.id, c.name, c.cfu, c.professor, course, e.date, pr.* FROM exam e " +
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
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
        }
        return result;
    }
    
    public void insertRegistration (int student, int course, Date date, @Nullable Integer grade) throws TriggerSQLException {
        try {
            String sql = "INSERT INTO exam_result (student, course, date, grade) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, student);
            pstmt.setInt(2, course);
            pstmt.setDate(3, date);
            if(grade == null)
                pstmt.setNull(4, Types.INTEGER);
            else
                pstmt.setInt(4, grade);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
           TriggerSQLException.ifFromTrigger(ex);
        }
    }

    public static class TriggerSQLException extends Exception{
        public TriggerSQLException(String errString) {
            super(errString);
        }

        public static void ifFromTrigger(SQLException ex) throws TriggerSQLException{
            if(ex.getSQLState().equals("02000")) {
                throw new TriggerSQLException(ex.getMessage());
            }
        }
    }
}
