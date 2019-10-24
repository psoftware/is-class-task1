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
    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public int getGrade() {
        return grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Student getStudent () {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        Registration registration = (Registration)obj;
        return Objects.equals(registration.getExam(), this.getExam())
                && Objects.equals(registration.getGrade(), this.getGrade())
                && Objects.equals(registration.getStudent(), this.getStudent());
    }
}
