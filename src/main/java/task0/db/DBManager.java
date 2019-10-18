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
            String sql = "SELECT * FROM course WHERE professor = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, profID);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<Course>();
            while (rs.next()){
                Course c = new Course(rs.getInt("id"), rs.getString("name"), rs.getInt("cfu"), rs.getInt("professor"));
                result.add(c);
            }
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());           
        }
        return result;
    }
    
    public void insertExam (int courseID, LocalDate date) {
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
        }
    }
    
    public ArrayList<Registration> findRegistrationProfessor (int id) {
        ArrayList<Registration> result = null;
        try {
            String sql = "SELECT course, name, cfu, student, date, grade FROM exam_result e INNER JOIN course c ON c.id = e.course WHERE c.professor = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<Registration>();
            while (rs.next()) {
                Registration reg = new Registration(rs.getInt("student"), rs.getString("name"), rs.getInt("course"), rs.getDate("date"), rs.getInt("grade"));
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
            String sql;
            if (toDo) {
                sql = "SELECT course, name, cfu, date, grade FROM exam_result e INNER JOIN course c ON c.id = e.course WHERE e.student = ? AND grade is NULL";
            } else {
                sql = "SELECT course, name, cfu, date, grade FROM exam_result e INNER JOIN course c ON c.id = e.course WHERE e.student = ? AND grade is not NULL";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<Registration>();
            while (rs.next()) {
                Registration reg = new Registration(id, rs.getString("name"), rs.getInt("course"), rs.getDate("date"), rs.getInt("grade"));
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
            String sql = "SELECT course, date, name FROM exam e INNER JOIN course c ON c.id = e.course";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<>();
            while (rs.next()) {
                Exam e = new Exam(rs.getDate("date"), rs.getInt("course"), rs.getString("name"));
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
            if(ex.getErrorCode() == 02000) {
                throw new TriggerSQLException(ex.getMessage());
            }
        }
    }
}
