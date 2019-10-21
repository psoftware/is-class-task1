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
    private int grade;

    private Student student;
    private Exam exam;
    
    public Registration (Student student, Exam exam, int grade) {
        this.exam = exam;
        this.grade = grade;
        this.student = student;
    }

    public Exam getExam() {
        return exam;
    }
    public int getGrade() {
        return grade;
    }
    public Student getStudent () {
        return student;
    }
}
