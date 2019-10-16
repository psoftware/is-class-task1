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
public class Exam {
    private Date date;
    private int courseID;
    private String course;
    
    public Exam (Date date, int courseID, String course) {
        this.date = date;
        this.courseID = courseID;
        this.course = course;
    }
    
    public Date getDate () {
        return date;
    }
    
    public int getCourseID () {
        return courseID;
    }
    
    public String getCourse () {
        return course;
    }
}
