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
    private Integer grade;

    private Student student;
    private Exam exam;
    
    public Registration (Student student, Exam exam, Integer grade) {
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

    public Integer getGrade() {
        return grade;
    }
    public void setGrade(Integer grade) {
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

    @Override
    public String toString() {
        Student student = this.getStudent();
        Exam exam = this.getExam();
        Course course = exam.getCourse();
        Professor prof = course.getProfessor();

        return "Student: " + student.getId() + " " + student.getName() + " " + student.getSurname() + " "
                + "Professor: " + prof.getId() + " " + prof.getName() + " " + prof.getSurname() + " "
                + "Exam: " + exam.getDate() + " "
                + "Course: " + course.getId() + " "+ course.getName() + " " + course.getCfu();
    }
}
