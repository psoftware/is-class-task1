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
    private Course course;
    
    public Exam (Course course, Date date) {
        this.date = date;
        this.course = course;
    }
    
    public Date getDate () {
        return date;
    }
    
    public Course getCourse () {
        return course;
    }
}
