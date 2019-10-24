/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

import java.sql.Date;
import java.util.Objects;

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
    public void setDate(Date date) {
        this.date = date;
    }
    
    public Course getCourse () {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        Exam exam = (Exam)obj;
        return Objects.equals(exam.getCourse(), this.getCourse())
                && ((exam.getDate() == this.getDate()) ||
                (exam.getDate() != null && exam.getDate().toLocalDate().equals(this.getDate().toLocalDate())));
    }
}
