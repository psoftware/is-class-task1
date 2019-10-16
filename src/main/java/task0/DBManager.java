/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class DBManager {
    private String address = "localhost";
    private int port = 3306;
    private String DBName = "Task0";
    private String user = "root";
    private String password = "root";
    
    Connection conn = null;
    
    public DBManager (String address, int port, String DBName, String user, String password) {
        this.DBName = DBName;
        this.port = port;
        this.password = password;
        this.user = user;
        this.address = address;
        
        connect();
    }
    
    public void connect () {
        try {
            String url = "jdbc:mysql://" + address + ":" + Integer.toString(port) + "/" + DBName + "?&serverTimezone=UTC";
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
            String sql = "SELECT * FROM course WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, profID);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            result = new ArrayList<Course>();
            while (rs.next()){
                Course c = new Course(rs.getInt("id"), rs.getString("name"), rs.getInt("cfu"));
                result.add(c);
            }
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());           
        }
        return result;
    }
    
    public void insertExam (int courseID, Date date) {
        try {
            String sql = "INSERT INTO exam (course, date) VALUES(?, ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseID);
            pstmt.setDate(2, date);
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
            String sql = "SELECT course, name, cfu, student, date FROM exam_result e INNER JOIN course c ON c.id = e.course WHERE c.professor = ?";
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
                sql = "SELECT course, name, cfu, date FROM exam_result e INNER JOIN course c ON c.id = e.course WHERE e.student = ? AND grade is NULL";
            } else {
                sql = "SELECT course, name, cfu, date FROM exam_result e INNER JOIN course c ON c.id = e.course WHERE e.student = ? AND grade is not NULL";
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
    
    public void updateRegistration (int student, Date date, int course, int grade) {
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
    
    public void insertRegistration (int student, int course, Date date, int grade) {
        try {
            String sql = "INSERT INTO exam_result (student, course, date, grade) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, student);
            pstmt.setInt(2, course);
            pstmt.setDate(3, date);
            pstmt.setInt(4, grade);
        } catch (SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
