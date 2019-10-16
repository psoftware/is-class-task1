/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

import java.sql.Date;

/**
 *
 * @author cacomop
 */
public class Registration {
    private int studentID;
    private String course;
    private int courseID;
    private Date date;
    private int grade;
    
    public Registration (int studentID, String course, int courseID, Date date, int grade) {
        this.course = course;
        this.date = date;
        this.grade = grade;
        this.courseID = courseID;
        this.studentID = studentID;
    }
    
    public String getCourse() {
        return course;
    }
    
    public Date getDate() {
        return date;
    }
    
    public int getGrade() {
        return grade;
    }
    
    public int getStudentID () {
        return studentID;
    }
    
    public int getCourseID () {
        return courseID;
    }
}
